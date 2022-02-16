package org.ton.parameters;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ton.executors.liteclient.api.ResultListParticipants;

import java.math.BigInteger;
import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class ValidationParam {
    Long totalNodes;
    Long validatorNodes;
    Long blockchainLaunchTime;
    Long startCycle;
    Long endCycle;
    Long startElections;
    Long endElections;
    Long nextElections;
    String minterAddr;
    String configAddr;
    String electorAddr;
    Long electionDuration;
    Long validationDuration;
    Long holdPeriod;
    BigInteger minStake;
    BigInteger maxStake;
    List<ResultListParticipants> participants;
}