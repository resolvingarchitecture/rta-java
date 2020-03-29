package ra.rta.rfm.conspref.models;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import ra.rta.models.KPI;

public interface AgeKPIs {

    ra.rta.models.KPI CHILD = new ra.rta.models.KPI(543535345);
    ra.rta.models.KPI TEEN = new ra.rta.models.KPI(2355325);
    ra.rta.models.KPI LATE_TEEN = new ra.rta.models.KPI(253525);
    ra.rta.models.KPI YOUNG_ADULT = new ra.rta.models.KPI(2532353);
    ra.rta.models.KPI ADULT = new ra.rta.models.KPI(53252532);
    ra.rta.models.KPI FAMILY = new ra.rta.models.KPI(62662463);
    ra.rta.models.KPI MIDLIFE = new ra.rta.models.KPI(2346626);
    ra.rta.models.KPI EMPTY_NEST = new ra.rta.models.KPI(26423634);
    ra.rta.models.KPI RETIREMENT = new ra.rta.models.KPI(264326);
    ra.rta.models.KPI SILVER = new ra.rta.models.KPI(26436643);

    Set<KPI> ALL = ImmutableSet.of(CHILD, TEEN, LATE_TEEN, YOUNG_ADULT, ADULT, FAMILY,
            MIDLIFE, EMPTY_NEST, RETIREMENT, SILVER);

}
