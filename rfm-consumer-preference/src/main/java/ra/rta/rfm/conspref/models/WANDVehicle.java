package ra.rta.rfm.conspref.models;

public enum WANDVehicle {
    unknown, deposit, loan, card, cd, misc;

    public static WANDVehicle determineWANDVehicle(FinancialTransaction financialTransaction) {
        return unknown;
    }
}
