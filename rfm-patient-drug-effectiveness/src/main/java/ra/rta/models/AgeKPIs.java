package ra.rta.models;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public interface AgeKPIs {

    KPI CHILD = new KPI(543535345);
    KPI TEEN = new KPI(2355325);
    KPI LATE_TEEN = new KPI(253525);
    KPI YOUNG_ADULT = new KPI(2532353);
    KPI ADULT = new KPI(53252532);
    KPI FAMILY = new KPI(62662463);
    KPI MIDLIFE = new KPI(2346626);
    KPI EMPTY_NEST = new KPI(26423634);
    KPI RETIREMENT = new KPI(264326);
    KPI SILVER = new KPI(26436643);

    Set<KPI> ALL = ImmutableSet.of(CHILD, TEEN, LATE_TEEN, YOUNG_ADULT, ADULT, FAMILY,
            MIDLIFE, EMPTY_NEST, RETIREMENT, SILVER);

}
