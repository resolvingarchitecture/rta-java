package ra.rta.models;

public enum WANDVehicle {
    unknown, deposit, loan, card, cd, misc;

    public static WANDVehicle determineWANDVehicle(Transaction transaction) {
        return unknown;
    }
}
