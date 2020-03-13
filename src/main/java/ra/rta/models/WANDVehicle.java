package ra.rta.models;

public enum WANDVehicle {
    unknown, deposit, loan, card, cd, misc;

    public static WANDVehicle determineWANDVehicle(Transaction transaction) {
        if(transaction instanceof DepositTransaction) return deposit;
        return unknown;
    }
}
