package edu.boardgames.collections.explorer.infrastructure.bgg;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Positive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageTest {
    @Test
    @Disabled("dummy test to make page test runnable")
    void dummyTestToMakePageTestRunnable() {
    }

    @Group
    class ConstructionTests {
        @Example
        void failsWhenSizeIsZero() {
            assertThatThrownBy(() -> new Page(0))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Page size must be greater than 0");
        }

        @Property
        void failsWhenSizeNotPositive(@ForAll @Negative int size) {
            assertThatThrownBy(() -> new Page(size))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Page size must be greater than 0");
        }

        @Property
        void succeedsWhenSizeIsPositive(@ForAll @Positive int size) {
            assertThat(new Page(size)).isNotNull();
        }
    }

    @Group
    class CountTests {
        @Property
        void zeroRecordsAreAlwaysZeroPages(@ForAll @Positive int size) {
            assertThat(new Page(size).count(0)).isZero();
        }

        @Property
        void recordCountSmallerThanOrEqualToPageSizeIsAlwaysOnePage(@ForAll("lessRecordsThanPageSize") PageInput input) {
            assertThat(new Page(input.pageSize()).count(input.recordCount())).isOne();
        }

        @Property
        void recordCountGreaterThanPageSizeIsMoreThanOnePage(@ForAll("moreRecordsThanPageSize") PageInput input) {
            Assume.that(input.pageSize() < Integer.MAX_VALUE);
            assertThat(new Page(input.pageSize()).count(input.recordCount())).isGreaterThan(1);
        }
    }

    @Group
    class AveragePageSizeTests {
        @Property
        void zeroRecordsResultIntoAveragePageSizeOfZero(@ForAll @Positive int size) {
            assertThat(new Page(size).averageSize(0)).isZero();
        }

        @Property
        void recordCountSmallerThanOrEqualToPageSizeIsAlwaysOnePage(@ForAll @Positive int recordCount, @ForAll @Positive int size) {
            assertThat(new Page(size).averageSize(recordCount)).isPositive().isLessThanOrEqualTo(size);
        }
    }

    @Provide
    Arbitrary<PageInput> lessRecordsThanPageSize(@ForAll @Positive int size) {
        return Arbitraries.integers()
                .greaterOrEqual(1)
                .lessOrEqual(size)
                .map(recordCount -> new PageInput(recordCount, size));
    }

    @Provide
    Arbitrary<PageInput> moreRecordsThanPageSize(@ForAll @Positive int size) {
        return Arbitraries.integers()
                .greaterOrEqual(size + 1)
                .map(recordCount -> new PageInput(recordCount, size));
    }

    private record PageInput(int recordCount, int pageSize) {
    }
}
