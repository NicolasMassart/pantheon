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
package tech.pegasys.pantheon.tests.acceptance.ibft2;

import tech.pegasys.pantheon.tests.acceptance.dsl.AcceptanceTestBase;
import tech.pegasys.pantheon.tests.acceptance.dsl.node.PantheonNode;

import java.io.IOException;

import org.junit.Test;

public class Ibft2ProposalRpcAcceptanceTest extends AcceptanceTestBase {

  @Test
  public void shouldReturnProposals() throws IOException {
    final String[] validators = {"validator1", "validator2", "validator3"};
    final PantheonNode validator1 =
        pantheon.createIbft2NodeWithValidators("validator1", validators);
    final PantheonNode validator2 =
        pantheon.createIbft2NodeWithValidators("validator2", validators);
    final PantheonNode validator3 =
        pantheon.createIbft2NodeWithValidators("validator3", validators);
    cluster.start(validator1, validator2, validator3);

    cluster.verify(ibftTwo.noProposals());
    validator1.execute(ibftTwoTransactions.createAddProposal(validator3));
    validator1.execute(ibftTwoTransactions.createRemoveProposal(validator2));
    validator2.execute(ibftTwoTransactions.createRemoveProposal(validator3));

    validator1.verify(
        ibftTwo.pendingVotesEqual().addProposal(validator3).removeProposal(validator2).build());
    validator2.verify(ibftTwo.pendingVotesEqual().removeProposal(validator3).build());
    validator3.verify(ibftTwo.noProposals());
  }
}
