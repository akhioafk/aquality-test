package com.alabs.automation.phoenix.data.formula;

import com.alabs.automation.framework.utilities.Randomizer;
import com.alabs.automation.phoenix.models.formula.snapshot.FormulaSnapshotAttributes;
import com.alabs.automation.phoenix.models.formula.snapshot.FormulaSnapshotValue;
import com.alabs.automation.phoenix.models.formula.snapshot.FormulaSnapshotRequest;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.stream.IntStream;

import static com.alabs.automation.phoenix.constants.formulaservice.FormulaGenerationConstants.MAX_VALUE;

@UtilityClass
public class FormulaSnapshotGenerator {
    private static final int SEVERAL_SNAPSHOT_COUNT = 2;

    public static FormulaSnapshotRequest prepareSnapshotRequestWithoutParameter() {
        return FormulaSnapshotRequest.builder().build();
    }

    public static FormulaSnapshotRequest prepareSnapshotRequestWithNonExistingId(String gameAccountId) {
        return prepareSnapshotRequest(gameAccountId, prepareSnapshotWithIdOnly());
    }

    public static FormulaSnapshotRequest prepareValidSnapshotRequest(String gameAccountId) {
        FormulaSnapshotValue snapshot = prepareSnapshotWithIdOnly();
        snapshot.setAdditionalAttributes(prepareSnapshotAttributes());
        return prepareSnapshotRequest(gameAccountId, snapshot);
    }

    public static FormulaSnapshotRequest prepareSnapshotRequestWithExistingId(String gameAccountId, String snapshotId) {
        return prepareSnapshotRequest(gameAccountId, prepareSnapshotWithIdOnly()
                .toBuilder()
                .snapshotId(snapshotId)
                .additionalAttributes(prepareSnapshotAttributes())
                .build());
    }

    public static FormulaSnapshotRequest prepareSnapshotRequestWithSeveralId(String gameAccountId) {
        FormulaSnapshotValue[] snapshots = IntStream.range(0, SEVERAL_SNAPSHOT_COUNT)
                .mapToObj(i -> {
                    FormulaSnapshotValue snapshot = prepareSnapshotWithIdOnly();
                    snapshot.setAdditionalAttributes(prepareSnapshotAttributes());
                    return snapshot;
                })
                .toArray(FormulaSnapshotValue[]::new);
        return prepareSnapshotRequest(gameAccountId, snapshots);
    }

    private static FormulaSnapshotRequest prepareSnapshotRequest(String gameAccountId, FormulaSnapshotValue... snapshots) {
        return prepareSnapshotRequestWithoutParameter().toBuilder()
                .gameAccountId(gameAccountId)
                .snapshots(snapshots)
                .build();
    }

    private static FormulaSnapshotValue prepareSnapshotWithIdOnly() {
        return FormulaSnapshotValue.builder()
                .snapshotId(UUID.randomUUID().toString())
                .build();
    }

    private static FormulaSnapshotAttributes prepareSnapshotAttributes() {
        return FormulaSnapshotAttributes.builder().testAttribute(Randomizer.randomPositiveInt(MAX_VALUE)).build();
    }
}
