/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.trie;

import static com.google.common.base.Preconditions.checkNotNull;
import static tech.pegasys.pantheon.ethereum.trie.CompactEncoding.bytesToPath;

import tech.pegasys.pantheon.util.bytes.Bytes32;
import tech.pegasys.pantheon.util.bytes.BytesValue;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link MerklePatriciaTrie} that persists trie nodes to a {@link MerkleStorage} key/value store.
 *
 * @param <V> The type of values stored by this trie.
 */
public class StoredMerklePatriciaTrie<K extends BytesValue, V> implements MerklePatriciaTrie<K, V> {
  private final GetVisitor<V> getVisitor = new GetVisitor<>();
  private final RemoveVisitor<V> removeVisitor = new RemoveVisitor<>();
  private final StoredNodeFactory<V> nodeFactory;

  private Node<V> root;

  /**
   * Create a trie.
   *
   * @param nodeLoader The {@link NodeLoader} to retrieve node data from.
   * @param valueSerializer A function for serializing values to bytes.
   * @param valueDeserializer A function for deserializing values from bytes.
   */
  public StoredMerklePatriciaTrie(
      final NodeLoader nodeLoader,
      final Function<V, BytesValue> valueSerializer,
      final Function<BytesValue, V> valueDeserializer) {
    this(nodeLoader, MerklePatriciaTrie.EMPTY_TRIE_NODE_HASH, valueSerializer, valueDeserializer);
  }

  /**
   * Create a trie.
   *
   * @param nodeLoader The {@link NodeLoader} to retrieve node data from.
   * @param rootHash The initial root has for the trie, which should be already present in {@code
   *     storage}.
   * @param valueSerializer A function for serializing values to bytes.
   * @param valueDeserializer A function for deserializing values from bytes.
   */
  public StoredMerklePatriciaTrie(
      final NodeLoader nodeLoader,
      final Bytes32 rootHash,
      final Function<V, BytesValue> valueSerializer,
      final Function<BytesValue, V> valueDeserializer) {
    this.nodeFactory = new StoredNodeFactory<>(nodeLoader, valueSerializer, valueDeserializer);
    this.root =
        rootHash.equals(MerklePatriciaTrie.EMPTY_TRIE_NODE_HASH)
            ? NullNode.instance()
            : new StoredNode<>(nodeFactory, rootHash);
  }

  @Override
  public Optional<V> get(final K key) {
    checkNotNull(key);
    return root.accept(getVisitor, bytesToPath(key)).getValue();
  }

  @Override
  public void put(final K key, final V value) {
    checkNotNull(key);
    checkNotNull(value);
    this.root = root.accept(new PutVisitor<>(nodeFactory, value), bytesToPath(key));
  }

  @Override
  public void remove(final K key) {
    checkNotNull(key);
    this.root = root.accept(removeVisitor, bytesToPath(key));
  }

  @Override
  public void commit(final NodeUpdater nodeUpdater) {
    final CommitVisitor<V> commitVisitor = new CommitVisitor<>(nodeUpdater);
    root.accept(commitVisitor);
    // Make sure root node was stored
    if (root.isDirty() && root.getRlpRef().size() < 32) {
      nodeUpdater.store(root.getHash(), root.getRlpRef());
    }
    // Reset root so dirty nodes can be garbage collected
    final Bytes32 rootHash = root.getHash();
    this.root =
        rootHash.equals(MerklePatriciaTrie.EMPTY_TRIE_NODE_HASH)
            ? NullNode.instance()
            : new StoredNode<>(nodeFactory, rootHash);
  }

  @Override
  public Map<Bytes32, V> entriesFrom(final Bytes32 startKeyHash, final int limit) {
    return StorageEntriesCollector.collectEntries(root, startKeyHash, limit);
  }

  @Override
  public void visitAll(final Consumer<Node<V>> visitor) {
    root.accept(new AllNodesVisitor<>(visitor));
  }

  @Override
  public Bytes32 getRootHash() {
    return root.getHash();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + getRootHash() + "]";
  }
}
