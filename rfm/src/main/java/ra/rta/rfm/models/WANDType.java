package ra.rta.rfm.models;

public enum WANDType {

    unknown, debit, credit;

    public static WANDType determineWANDType(FinancialTransaction.Type type){
        switch(type) {
            case Credit: return credit;
            case Debit: return debit;
            case DepositReversal: return debit;
            case EstatementFee: return debit;
            case FeeReversal: return credit;
            case Float: return debit;
            case Interest: return credit;
            case MiscellaneousFee: return debit;
            case NonPost: return debit;
            case NonSufficientFundsFee: return debit;
            case Return: return credit;
            case System: return credit;
            case TruthInSavings: return debit;
            case Waived: return credit;
            default: return unknown;
        }
    }

}
