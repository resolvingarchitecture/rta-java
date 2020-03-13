package ra.rta.models.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class FileGenerator {

    public static final String CUSTOMER_REGISTRATION_FIXED_WIDTH_COMMAND_PATH_JSON = "customer_registration_fixed_width";

    @SuppressWarnings("resource")
    public static void main(String[] args) {

        try {
            String stagingFolder = args[0];
            int numberOfCustomers = Integer.parseInt(args[1]);
            int numberOfDays = Integer.parseInt(args[2]);
            int flattened = Integer.parseInt(args[3]);
            // TODO replace w/ SLF4J
            System.out.println("Creating " + numberOfCustomers
                    + " Customers with randomized Accounts and Transactions over " + numberOfDays + " number of days "
                    + (flattened == 1 ? "flattened" : "relational") + "...");

            List<String> termcodeLeafs = new ArrayList<>(30);
            // Non-Competitive
            termcodeLeafs.add("HOTEL OH US");
            termcodeLeafs.add("MOTEL NV USA89014");
            termcodeLeafs.add("TARGET T2087");
            // Competitive
            termcodeLeafs.add("BMO HARRIS BANK NA MOORESVILLE IN");
            termcodeLeafs.add("REDBRAND CREDIT UNION VISA");


            File customersFile = new File(stagingFolder + CUSTOMER_REGISTRATION_FIXED_WIDTH_COMMAND_PATH_JSON+"/"+ CUSTOMER_REGISTRATION_FIXED_WIDTH_COMMAND_PATH_JSON+".txt");
            if (!customersFile.exists()) {
                customersFile.createNewFile();
            }
            PrintWriter customersWriter = new PrintWriter(new BufferedWriter(new FileWriter(customersFile)));

            //            File interactionsFile = new File(stagingFolder + Interaction.CUSTOMER_INTERACTION_FIXED_WIDTH_COMMAND_PATH_JSON +"/"+Interaction.CUSTOMER_INTERACTION_FIXED_WIDTH_COMMAND_PATH_JSON+".txt");
            //            if(!interactionsFile.exists()) interactionsFile.createNewFile();
            //            PrintWriter interactionsWriter = new PrintWriter(new BufferedWriter(new FileWriter(interactionsFile)));
            //
            //            File applicationsFile = new File(stagingFolder + Application.APPLICATION_FIXED_WIDTH_COMMAND_PATH_JSON +"/"+Application.APPLICATION_FIXED_WIDTH_COMMAND_PATH_JSON+".txt");
            //            if(!applicationsFile.exists()) applicationsFile.createNewFile();
            //            PrintWriter applicationsWriter = new PrintWriter(new BufferedWriter(new FileWriter(applicationsFile)));

            File cardAccountsFile = new File(stagingFolder +"card_account_fixed_width/card_account_fixed_width.txt");
            if (!cardAccountsFile.exists()) {
                cardAccountsFile.createNewFile();
            }
            PrintWriter cardAccountsWriter = new PrintWriter(new BufferedWriter(new FileWriter(cardAccountsFile)));

            File depositAccountsFile = new File(stagingFolder + "deposit_account_fixed_width/deposit_account_fixed_width.txt");
            if (!depositAccountsFile.exists()) {
                depositAccountsFile.createNewFile();
            }
            PrintWriter depositAccountsWriter = new PrintWriter(new BufferedWriter(new FileWriter(depositAccountsFile)));

            File insuranceAccountsFile = new File(stagingFolder + "insurance_account_fixed_width/insurance_account_fixed_width.txt");
            if (!insuranceAccountsFile.exists()) {
                insuranceAccountsFile.createNewFile();
            }
            PrintWriter insuranceAccountsWriter = new PrintWriter(new BufferedWriter(new FileWriter(insuranceAccountsFile)));

            File lineAccountsFile = new File(stagingFolder + "line_account_fixed_width/line_account_fixed_width.txt");
            if (!lineAccountsFile.exists()) {
                lineAccountsFile.createNewFile();
            }
            PrintWriter lineAccountsWriter = new PrintWriter(new BufferedWriter(new FileWriter(lineAccountsFile)));

            File loanAccountsFile = new File(stagingFolder + "loan_account_fixed_width/loan_account_fixed_width.txt");
            if (!loanAccountsFile.exists()) {
                loanAccountsFile.createNewFile();
            }
            PrintWriter loanAccountsWriter = new PrintWriter(new BufferedWriter(new FileWriter(loanAccountsFile)));

            File cardTransactionsFile = new File(stagingFolder + "card_transaction_fixed_width/card_transaction_fixed_width.txt");
            if (!cardTransactionsFile.exists()) {
                cardTransactionsFile.createNewFile();
            }
            PrintWriter cardTransactionsWriter = new PrintWriter(new BufferedWriter(new FileWriter(cardTransactionsFile)));

            File depositTransactionsFile = new File(stagingFolder + "deposit_transaction_fixed_width/deposit_transaction_fixed_width.txt");
            if (!depositTransactionsFile.exists()) {
                depositTransactionsFile.createNewFile();
            }
            PrintWriter depositTransactionsWriter = new PrintWriter(new BufferedWriter(new FileWriter(depositTransactionsFile)));

            File insuranceTransactionsFile = new File(stagingFolder + "insurance_transaction_fixed_width/insurance_transaction_fixed_width.txt");
            if (!insuranceTransactionsFile.exists()) {
                insuranceTransactionsFile.createNewFile();
            }
            PrintWriter insuranceTransactionsWriter = new PrintWriter(new BufferedWriter(new FileWriter(insuranceTransactionsFile)));

            File loanTransactionsFile = new File(stagingFolder + "loan_transaction_fixed_width/loan_transaction_fixed_width.txt");
            if (!loanTransactionsFile.exists()) {
                loanTransactionsFile.createNewFile();
            }
            PrintWriter loanTransactionsWriter = new PrintWriter(new BufferedWriter(new FileWriter(loanTransactionsFile)));

            String partnerName = "p1";
            Calendar today = Calendar.getInstance();
            Calendar endDate = (Calendar)today.clone();
            endDate.add(Calendar.DATE,-1); // yesterday
            Calendar startDate = (Calendar)endDate.clone();
            startDate.add(Calendar.DATE, numberOfDays); // Go back number of days from yesterday
            Calendar transStartDate = (Calendar)startDate.clone();
            Calendar cloned;

            for (int i1 = 0; i1 < numberOfCustomers; i1++) {
                String ucid = Generator.adic(partnerName, "" + i1);

                // Customer
                StringBuffer customerSB = new StringBuffer();
                customerSB.append(String.format("%-50s", ucid)); // ucid
                customerSB.append(String.format("%-50s", ucid)); // uhid
                Calendar customerOpenDate = (Calendar)endDate.clone();
                customerOpenDate.add(Calendar.DATE, -new Random().nextInt(numberOfDays));
                customerSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(customerOpenDate.getTime()))); // open_date;
                customerSB.append(String.format("%-15s", "9999-12-31")); // close_date;
                customerSB.append(String.format("%-15s", "16")); // bank_branch;
                customerSB.append(String.format("%-15s", "0")); // status;
                customerSB.append(String.format("%-15s", "USA")); // country;
                customerSB.append(String.format("%-15s", "98337")); // zip_code;
                customerSB.append(String.format("%-15s", new Random().nextInt(2010-1900)+1900)); // birth_year;
                customerSB.append(String.format("%-15s", "0")); // deceased_year;
                customerSB.append(String.format("%-15s", "0")); // type;
                customerSB.append(String.format("%-15s", "WA")); // state;
                customerSB.append(String.format("%-15s", new Random().nextInt(1))); // gender;
                customerSB.append(String.format("%-15s", new Random().nextInt(1))); // marital_status;
                customerSB.append(String.format("%-15s", new Random().nextInt(10000000))); // investable_assets;
                customerSB.append(String.format("%-15s", "Y")); // estatement_indicator;
                customerSB.append(String.format("%-15s", new Random().nextInt(1))); // glba_opt_out;
                customerSB.append(String.format("%-15s", new Random().nextInt(1))); // solicitation_code;
                customerSB.append(String.format("%-15s", "100")); // segmentation;
                customerSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()))); // business_inception_date;
                customerSB.append(String.format("%-15s", "100")); // business_sic_code;
                customerSB.append(String.format("%-15s", "0")); // business_naics_code;
                customerSB.append(String.format("%-15s", new Random().nextInt(100000000))); // business_annual_sales;
                customerSB.append(String.format("%-15s", new Random().nextInt(998)+1)); // business_num_employees;
                customersWriter.print(customerSB.toString() + "\n");

                // Interactions
                //                sb = new StringBuffer();
                //                int numberOfInteractions = new Random().nextInt(9)+1;
                //                for(int m = 0; m < numberOfInteractions; m++) {
                //                    sb.append(String.format("%-50s", ucid)); // ucid
                //                    sb.append(String.format("%-50s", ucid)); // uhic
                //                    sb.append(String.format("%-50s", ucid)); // uaic
                //                    sb.append(String.format("%-15s", new Random().nextInt(1) + 901)); // reltype
                //                    sb.append(String.format("%-15s", new Random().nextInt(4) + 1)); // trantype
                //                    sb.append(String.format("%-15s", new Random().nextInt(5))); // channel
                //                    cloned = (Calendar) today.clone();
                //                    cloned.add(Calendar.MINUTE, -new Random().nextInt(60 * 23));
                //                    sb.append(String.format("%-30s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // date_time
                //                    sb.append(String.format("%-300s", "JEFFERSON COUNTY CONSERVATION")); // memo
                //                    interactionsWriter.print(sb.toString() + "\n");
                //                }

                // Deposit Account
                StringBuffer depositAccountSB = new StringBuffer(); // Checking
                depositAccountSB.append(String.format("%-50s", ucid)); // ucid
                depositAccountSB.append(String.format("%-50s", ucid)); // uhid
                String uaid = Generator.adic("ksp", ucid + new Random().nextInt(1000));
                depositAccountSB.append(String.format("%-50s", uaid)); // uaic
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(1) + 901)); // reltype
                depositAccountSB.append(String.format("%-15s", "DP")); // type
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(999999999))); // subtype
                depositAccountSB.append(String.format("%-15s", "Y")); // estatement_indicator
                depositAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(customerOpenDate.getTime()))); // open_date
                depositAccountSB.append(String.format("%-15s", "")); // close_date
                int balance = new Random().nextInt(100000);
                depositAccountSB.append(String.format("%-15s", balance)); // balance
                depositAccountSB.append(String.format("%-15s", balance)); // available_balance
                depositAccountSB.append(String.format("%-15s", "")); // maturity_date
                depositAccountSB.append(String.format("%-15s", "")); // renewal_date
                depositAccountSB.append(String.format("%-15s", 0)); // renewal_times
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(1))); // overdraft_protection
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(4))); // interest_rate
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(150))); // interest_earned_ytd
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(300))); // prev_year_interest
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(300))); // fees_paid_ytd
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(15))); // fees_paid_mtd
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(150))); // fees_waived_ytd
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(15))); // fees_waived_mtd
                depositAccountSB.append(String.format("%-15s", "")); // last_overdraft_date
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(4))); // num_overdrawn_ytd
                depositAccountSB.append(String.format("%-15s", new Random().nextInt(1))); // num_overdrawn_mtd
                depositAccountsWriter.print(depositAccountSB.toString() + "\n");

                // Deposit (Checking) Transactions
                int numberDepositTransactions = 1; // Bi-Weekly deposits
                if(numberOfDays > 14) {
                    numberDepositTransactions = Math.round(numberOfDays / 14);
                }
                for(int i2=0; i2 < numberDepositTransactions; i2++) {
                    Calendar customerTransStartDate = (Calendar) customerOpenDate.clone();
                    StringBuffer depositTransactionSB = new StringBuffer();
                    depositTransactionSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                    depositTransactionSB.append(String.format("%-50s", ucid)); // uhid
                    depositTransactionSB.append(String.format("%-50s", uaid)); // uaid
                    depositTransactionSB.append(String.format("%-15s", "902")); // reltype
                    depositTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(customerTransStartDate.getTime()))); // date
                    cloned = (Calendar) customerTransStartDate.clone();
                    cloned.add(Calendar.MINUTE, new Random().nextInt(60 * 23));
                    depositTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // post_date
                    depositTransactionSB.append(String.format("%-15s", "500")); // type
                    depositTransactionSB.append(String.format("%-15s", "US")); // mrkting_code
                    depositTransactionSB.append(String.format("%-15s", "Posted")); // status
                    depositTransactionSB.append(String.format("%-15s", new Random().nextInt(999) + 1)); // amount
                    depositTransactionSB.append(String.format("%-15s", new Random().nextInt(8999) + 1000)); // zip_code
                    depositTransactionSB.append(String.format("%-15s", "100")); // merchant_id
                    depositTransactionSB.append(String.format("%-15s", "8042")); // sic_code
                    depositTransactionSB.append(String.format("%-15s", "50773")); // naics_code
                    depositTransactionSB.append(String.format("%-15s", "5969589")); // mcc
                    depositTransactionSB.append(String.format("%-15s", "1")); // intrchg_rate
                    depositTransactionSB.append(String.format("%-15s", "USD")); // currency_id
                    depositTransactionSB.append(String.format("%-15s", "1")); // currency_conv_rate
                    depositTransactionSB.append(String.format("%-300s", termcodeLeafs.get(new Random().nextInt(29)))); // payee
                    depositTransactionSB.append(String.format("%-300s", "PORT HADLOCK VISION CL")); // memo
                    depositTransactionsWriter.print(depositTransactionSB.toString() + "\n");
                }

                // Secondary Accounts (2)
                for(int k=0; k<2; k++) {
                    int accountType = new Random().nextInt(5);
                    switch (accountType) {
                        case 0: {
                            Calendar cardAccountOpenDate = (Calendar) customerOpenDate.clone();
                            cardAccountOpenDate.add(Calendar.DATE, new Random().nextInt(numberOfDays) + 30);
                            long cardAccountBand = DateUtility.daysDifference(endDate.getTime(), cardAccountOpenDate.getTime());
                            int numberCardTransactionsPerDay = 4;
                            uaid = Generator.adic(partnerName, ucid + new Random().nextInt(1000));
                            // Card Transactions
                            int totalNumberTransactions = (int)cardAccountBand * numberCardTransactionsPerDay;
                            Calendar cardTransDate = (Calendar)cardAccountOpenDate.clone();
                            Calendar lastPaymentDate;
                            for (int j = 0; j < totalNumberTransactions; j++) {
                                StringBuffer cardTransactionSB = new StringBuffer();
                                cardTransactionSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                                cardTransactionSB.append(String.format("%-50s", ucid)); // uhid
                                cardTransactionSB.append(String.format("%-50s", uaid)); // uaid
                                cardTransactionSB.append(String.format("%-15s", "902")); // reltype
                                cardTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cardTransDate.getTime()))); // date
                                cloned = (Calendar) cardTransDate.clone();
                                cloned.add(Calendar.MINUTE, new Random().nextInt(60 * 23));
                                cardTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // post_date
                                cardTransactionSB.append(String.format("%-15s", "500")); // type
                                cardTransactionSB.append(String.format("%-15s", "US")); // mrkting_code
                                cardTransactionSB.append(String.format("%-15s", "Posted")); // status
                                cardTransactionSB.append(String.format("%-15s", new Random().nextInt(999) + 1)); // amount
                                cardTransactionSB.append(String.format("%-15s", new Random().nextInt(8999) + 1000)); // zip_code
                                cardTransactionSB.append(String.format("%-15s", "100")); // merchant_id
                                cardTransactionSB.append(String.format("%-15s", "8042")); // sic_code
                                cardTransactionSB.append(String.format("%-15s", "50773")); // naics_code
                                cardTransactionSB.append(String.format("%-15s", "5969589")); // mcc
                                cardTransactionSB.append(String.format("%-15s", "1")); // intrchg_rate
                                cardTransactionSB.append(String.format("%-15s", "USD")); // currency_id
                                cardTransactionSB.append(String.format("%-15s", "1")); // currency_conv_rate
                                cardTransactionSB.append(String.format("%-300s", termcodeLeafs.get(new Random().nextInt(29)))); // payee
                                cardTransactionSB.append(String.format("%-300s", "PORT HADLOCK VISION CL")); // memo
                                cardTransactionsWriter.print(cardTransactionSB.toString() + "\n");
                                cardTransDate.add(Calendar.HOUR, 3);
                            }
                            // Card Account
                            StringBuffer cardAccountSB = new StringBuffer();
                            cardAccountSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                            cardAccountSB.append(String.format("%-50s", ucid)); // uhid
                            cardAccountSB.append(String.format("%-50s", uaid)); // uaid
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(1) + 901)); // reltype
                            cardAccountSB.append(String.format("%-15s", "CC")); // type
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // company_id
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(1))); // subtype
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // subtype_code1
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // subtype_code2
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // subtype_code3
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // pricing_strategy_code1
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // pricing_strategy_code2
                            cardAccountSB.append(String.format("%-50s", new Random().nextInt(999999998) + 1)); // misc_user1
                            cardAccountSB.append(String.format("%-50s", new Random().nextInt(999999998) + 1)); // misc_user2
                            cardAccountSB.append(String.format("%-50s", new Random().nextInt(999999998) + 1)); // misc_user3
                            cardAccountSB.append(String.format("%-50s", new Random().nextInt(998) + 1)); // affiliation
                            cardAccountSB.append(String.format("%-15s", "Y")); // estatement_indicator
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cardAccountOpenDate.getTime()))); // open_date
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(60) + 20)); // cost_of_funds_rate
                            cloned = (Calendar) endDate.clone();
                            cloned.add(Calendar.DATE, -new Random().nextInt(25));
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // last_payment_date
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // last_payment_amount
                            cloned = (Calendar) startDate.clone();
                            cloned.add(Calendar.DATE, new Random().nextInt(25));
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // next_due_date
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // next_amount_due
                            balance = new Random().nextInt(48500)+1500;
                            cardAccountSB.append(String.format("%-15s", balance - new Random().nextInt(200))); // average_daily_balance
                            cardAccountSB.append(String.format("%-15s", balance)); // balance
                            cardAccountSB.append(String.format("%-15s", balance - new Random().nextInt(600))); // merchpurch
                            cardAccountSB.append(String.format("%-15s", balance - new Random().nextInt(400))); // cash
                            cardAccountSB.append(String.format("%-15s", balance / 10)); // promo
                            int totalCreditLine = new Random().nextInt(100000) + balance;
                            cardAccountSB.append(String.format("%-15s", totalCreditLine)); // total_credit_line
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cardAccountOpenDate.getTime()))); // credit_line_date
                            cardAccountSB.append(String.format("%-15s", balance - new Random().nextInt(100))); // last_statement_balance
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(200))); // last_statement_payment
                            cardAccountSB.append(String.format("%-15s", 25)); // last_statement_latefee
                            cardAccountSB.append(String.format("%-15s", balance * .03 / 12)); // last_statement_interest
                            cardAccountSB.append(String.format("%-15s", 0)); // last_statement_ovrfee
                            cardAccountSB.append(String.format("%-15s", 0)); // last_statement_foreignfee
                            cardAccountSB.append(String.format("%-15s", 0)); // last_statement_miscfees
                            cardAccountSB.append(String.format("%-15s", 20)); // last_statement_transactionfee
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(200))); // last_statement_merch_purch
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(200))); // last_statement_cashamt
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(200))); // last_statement_checkamt
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(200))); // last_statement_promoamt
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(10))); // last_statement_cashnum
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(10))); // last_statement_merchnum
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(10))); // last_statement_checknum
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(10000))); // total_cash_limit
                            cardAccountSB.append(String.format("%-15s", 0)); // current_dpd
                            cardAccountSB.append(String.format("%-15s", 0)); // current_cpd
                            cardAccountSB.append(String.format("%-15s", "Active")); // status_code1
                            cardAccountSB.append(String.format("%-15s", "")); // status_code2
                            cardAccountSB.append(String.format("%-15s", "")); // status_code3
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cardAccountOpenDate.getTime()))); // status_code1_date
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cardAccountOpenDate.getTime()))); // status_code2_date
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cardAccountOpenDate.getTime()))); // status_code3_date
                            cardAccountSB.append(String.format("%-15s", balance + new Random().nextInt(1000))); // high_bal
                            cloned = (Calendar) startDate.clone();
                            cloned.add(Calendar.DATE, -new Random().nextInt(30));
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // date_highbal
                            cloned = (Calendar) startDate.clone();
                            cloned.add(Calendar.DATE, -new Random().nextInt(60));
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // date_last_overlimit
                            cardAccountSB.append(String.format("%-15s", balance + new Random().nextInt(1000))); // last_cash_amount
                            cloned = (Calendar) startDate.clone();
                            cloned.add(Calendar.DATE, -new Random().nextInt(6));
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // date_last_cash
                            cloned = (Calendar) startDate.clone();
                            cloned.add(Calendar.DATE, -new Random().nextInt(6));
                            cardAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // date_last_purch
                            cardAccountSB.append(String.format("%-15s", balance + new Random().nextInt(200))); // last_purch_amount
                            cardAccountSB.append(String.format("%-15s", 1)); // auth_user_flag
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(9999))); // payment_history12
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(9999))); // payment_history24
                            cardAccountSB.append(String.format("%-15s", totalCreditLine - balance)); // available_credit
                            cardAccountSB.append(String.format("%-15s", totalCreditLine - balance)); // available_cash
                            cardAccountSB.append(String.format("%-15s", (new Random().nextInt(20)+5)/100)); // cash_apr
                            cardAccountSB.append(String.format("%-15s", (new Random().nextInt(20)+5)/100)); // merch_apr
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(50))); // fees_waived_mtd
                            cardAccountSB.append(String.format("%-15s", new Random().nextInt(300))); // fees_waived_ytd
                            cardAccountsWriter.print(cardAccountSB.toString() + "\n");
                            break;
                        }
                        case 1: {
                            // Insurance Account
                            StringBuffer insuranceAccountSB = new StringBuffer();
                            uaid = Generator.adic(partnerName, ucid + new Random().nextInt(1000));
                            insuranceAccountSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                            insuranceAccountSB.append(String.format("%-50s", ucid)); // uhid
                            insuranceAccountSB.append(String.format("%-50s", uaid)); // uaid
                            insuranceAccountSB.append(String.format("%-15s", new Random().nextInt(999999))); // type
                            insuranceAccountSB.append(String.format("%-15s", new Random().nextInt(9999))); // subtype
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()))); // open_date
                            insuranceAccountSB.append(String.format("%-15s", "")); // close_date
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(today.getTime()))); // last_payment_date
                            insuranceAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // last_payment_amount
                            cloned = (Calendar) today.clone();
                            cloned.add(Calendar.MINUTE, new Random().nextInt(30));
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // next_due_date
                            insuranceAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // next_amount_due
                            insuranceAccountSB.append(String.format("%-15s", new Random().nextInt(9000)+1000)); // balance
                            insuranceAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // premium_amount
                            insuranceAccountSB.append(String.format("%-15s", new Random().nextInt(9000)+1000)); // remaining_balance
                            insuranceAccountSB.append(String.format("%-50s", new Random().nextInt(999999))); // vehicle_number
                            insuranceAccountSB.append(String.format("%-50s", "Land Rover")); // vehicle_make
                            insuranceAccountSB.append(String.format("%-50s", "Series IIa")); // vehicle_model
                            insuranceAccountSB.append(String.format("%-50s", 1971)); // vehicle_year
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()))); // vehicle_date_added
                            insuranceAccountSB.append(String.format("%-15s", 37900)); // cash_value
                            insuranceAccountSB.append(String.format("%-50s", 250000)); // death_benefit
                            insuranceAccountSB.append(String.format("%-50s", new Random().nextInt(60))); // policy_term
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()))); // policy_term_from_date
                            cloned = (Calendar) startDate.clone();
                            cloned.add(Calendar.YEAR, new Random().nextInt(10));
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // policy_term_to_date
                            insuranceAccountSB.append(String.format("%-50s", new Random().nextInt(200000)+10000)); // liability_limit_single
                            insuranceAccountSB.append(String.format("%-50s", new Random().nextInt(2000000)+100000)); // liability_limit_total
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()))); // effective_date
                            insuranceAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // expiration_date
                            insuranceAccountSB.append(String.format("%-300s", "Bitcoin Credit Union")); // vehicle_lienholder
                            insuranceAccountsWriter.print(insuranceAccountSB.toString() + "\n");

                            // Insurance Transactions
                            StringBuffer insuranceTransactionSB = new StringBuffer();
                            insuranceTransactionSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                            insuranceTransactionSB.append(String.format("%-50s", ucid)); // uhic
                            insuranceTransactionSB.append(String.format("%-50s", ucid)); // uaic
                            insuranceTransactionSB.append(String.format("%-15s", "902")); // reltype
                            insuranceTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(transStartDate.getTime()))); // date
                            // Add a day
                            cloned = (Calendar) transStartDate.clone();
                            cloned.add(Calendar.MINUTE, new Random().nextInt(60 * 23));
                            insuranceTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // post_date
                            insuranceTransactionSB.append(String.format("%-15s", "500")); // type
                            insuranceTransactionSB.append(String.format("%-15s", "US")); // mrkting_code
                            insuranceTransactionSB.append(String.format("%-15s", "Posted")); // status
                            insuranceTransactionSB.append(String.format("%-15s", new Random().nextInt(999) + 1)); // amount
                            insuranceTransactionSB.append(String.format("%-15s", new Random().nextInt(8999) + 1000)); // zip_code
                            insuranceTransactionSB.append(String.format("%-15s", "100")); // merchant_id
                            insuranceTransactionSB.append(String.format("%-15s", "8042")); // sic_code
                            insuranceTransactionSB.append(String.format("%-15s", "50773")); // naics_code
                            insuranceTransactionSB.append(String.format("%-15s", "5969589")); // mcc
                            insuranceTransactionSB.append(String.format("%-15s", "1")); // intrchg_rate
                            insuranceTransactionSB.append(String.format("%-15s", "USD")); // currency_id
                            insuranceTransactionSB.append(String.format("%-15s", "1")); // currency_conv_rate
                            insuranceTransactionSB.append(String.format("%-300s", termcodeLeafs.get(new Random().nextInt(29)))); // payee
                            insuranceTransactionSB.append(String.format("%-300s", "PORT HADLOCK VISION CL")); // memo
                            insuranceTransactionsWriter.print(insuranceTransactionSB.toString() + "\n");
                            break;
                        }
                        case 3: {
                            // Loan Account
                            StringBuffer loanAccountSB = new StringBuffer();
                            uaid = Generator.adic("ksp", ucid + new Random().nextInt(1000));
                            loanAccountSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                            loanAccountSB.append(String.format("%-50s", ucid)); // uhid
                            loanAccountSB.append(String.format("%-50s", uaid)); // uaid
                            loanAccountSB.append(String.format("%-15s", "902")); // reltype
                            loanAccountSB.append(String.format("%-15s", "DP")); // type
                            loanAccountSB.append(String.format("%-15s", new Random().nextInt(999999999))); // subtype
                            loanAccountSB.append(String.format("%-15s", "")); // subtype_code1
                            loanAccountSB.append(String.format("%-15s", "")); // subtype_code2
                            loanAccountSB.append(String.format("%-15s", "")); // subtype_code3
                            loanAccountSB.append(String.format("%-15s", "N")); // estatement_indicator
                            loanAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()))); // open_date
                            loanAccountSB.append(String.format("%-15s", "")); // status_code
                            loanAccountSB.append(String.format("%-15s", "")); // status_date
                            loanAccountSB.append(String.format("%-15s", "")); // maturity_date
                            loanAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // remaining_pmt
                            loanAccountSB.append(String.format("%-15s", new Random().nextInt(60))); // term
                            cloned = (Calendar) today.clone();
                            cloned.add(Calendar.DATE, -new Random().nextInt(30));
                            loanAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // last_payment_date
                            loanAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // last_payment_amount
                            cloned = (Calendar) today.clone();
                            cloned.add(Calendar.DATE, new Random().nextInt(30));
                            loanAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // next_due_date
                            loanAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // next_amount_due
                            balance = new Random().nextInt(20000);
                            loanAccountSB.append(String.format("%-15s", balance)); // balance
                            loanAccountSB.append(String.format("%-15s", balance)); // principal_balance
                            loanAccountSB.append(String.format("%-15s", 3)); // apr
                            loanAccountSB.append(String.format("%-15s", 1)); // collateral_type
                            loanAccountSB.append(String.format("%-15s", new Random().nextInt(50000))); // collateral_value
                            loanAccountSB.append(String.format("%-50s", "Land Rover")); // vehicle_make
                            loanAccountSB.append(String.format("%-50s", "Series IIa")); // vehicle_model
                            loanAccountSB.append(String.format("%-50s", 1971)); // vehicle_year
                            loanAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // min_payment
                            loanAccountSB.append(String.format("%-15s", balance)); // ending_balance
                            int totalCreditLine = new Random().nextInt(5000)+balance;
                            loanAccountSB.append(String.format("%-15s", totalCreditLine)); // total_credit_line
                            loanAccountSB.append(String.format("%-15s", totalCreditLine - balance)); // available_credit
                            loanAccountSB.append(String.format("%-15s", 0)); // days_past_due
                            loanAccountSB.append(String.format("%-15s", 0)); // past_due_YTD
                            loanAccountSB.append(String.format("%-15s", 0)); // past_due_LTD
                            loanAccountsWriter.print(loanAccountSB.toString() + "\n");

                            // Loan Transaction
                            StringBuffer loanTransactionSB = new StringBuffer();
                            loanTransactionSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                            loanTransactionSB.append(String.format("%-50s", ucid)); // uhid
                            loanTransactionSB.append(String.format("%-50s", uaid)); // uaid
                            loanTransactionSB.append(String.format("%-15s", "902")); // reltype
                            loanTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(transStartDate.getTime()))); // date
                            cloned = (Calendar) transStartDate.clone();
                            cloned.add(Calendar.MINUTE, new Random().nextInt(60 * 23));
                            loanTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // post_date
                            loanTransactionSB.append(String.format("%-15s", "500")); // type
                            loanTransactionSB.append(String.format("%-15s", "US")); // mrkting_code
                            loanTransactionSB.append(String.format("%-15s", "Posted")); // status
                            loanTransactionSB.append(String.format("%-15s", new Random().nextInt(999) + 1)); // amount
                            loanTransactionSB.append(String.format("%-15s", new Random().nextInt(8999) + 1000)); // zip_code
                            loanTransactionSB.append(String.format("%-15s", "100")); // merchant_id
                            loanTransactionSB.append(String.format("%-15s", "8042")); // sic_code
                            loanTransactionSB.append(String.format("%-15s", "50773")); // naics_code
                            loanTransactionSB.append(String.format("%-15s", "5969589")); // mcc
                            loanTransactionSB.append(String.format("%-15s", "1")); // intrchg_rate
                            loanTransactionSB.append(String.format("%-15s", "USD")); // currency_id
                            loanTransactionSB.append(String.format("%-15s", "1")); // currency_conv_rate
                            loanTransactionSB.append(String.format("%-300s", "PORT HADLOCK VISION CL")); // memo
                            loanTransactionsWriter.print(loanTransactionSB.toString() + "\n");
                        }
                        case 4: {
                            // Line Account
                            StringBuffer lineAccountSB = new StringBuffer();
                            uaid = Generator.adic("ksp", ucid + new Random().nextInt(1000));
                            lineAccountSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                            lineAccountSB.append(String.format("%-50s", ucid)); // uhid
                            lineAccountSB.append(String.format("%-50s", uaid)); // uaid
                            lineAccountSB.append(String.format("%-15s", "902")); // reltype
                            lineAccountSB.append(String.format("%-15s", "DP")); // type
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(999999999))); // subtype
                            lineAccountSB.append(String.format("%-15s", "")); // subtype_code1
                            lineAccountSB.append(String.format("%-15s", "")); // subtype_code2
                            lineAccountSB.append(String.format("%-15s", "")); // subtype_code3
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // pricing_strategy_code1
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(998) + 1)); // pricing_strategy_code2
                            lineAccountSB.append(String.format("%-15s", "Y")); // estatement_indicator
                            Calendar openDate = (Calendar) startDate.clone();
                            openDate.add(Calendar.DATE, -new Random().nextInt(365 * 10));
                            lineAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(openDate.getTime()))); // open_date
                            lineAccountSB.append(String.format("%-15s", "")); // status_code
                            lineAccountSB.append(String.format("%-15s", "")); // status_date
                            lineAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(openDate.getTime()))); // amortization_date
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // remaining_pmt
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(60))); // term
                            cloned = (Calendar) today.clone();
                            cloned.add(Calendar.DATE, -new Random().nextInt(30));
                            lineAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // last_payment_date
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // last_payment_amount
                            cloned = (Calendar) today.clone();
                            cloned.add(Calendar.DATE, new Random().nextInt(30));
                            lineAccountSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // next_due_date
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // next_amount_due
                            balance = new Random().nextInt(20000);
                            lineAccountSB.append(String.format("%-15s", balance)); // balance
                            lineAccountSB.append(String.format("%-15s", balance)); // principal_balance
                            lineAccountSB.append(String.format("%-15s", balance - new Random().nextInt(100))); // last_statement_balance
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(200))); // last_statement_payment
                            lineAccountSB.append(String.format("%-15s", balance * .03 / 12)); // last_statement_interest
                            lineAccountSB.append(String.format("%-15s", 25)); // last_statement_fees
                            lineAccountSB.append(String.format("%-15s", 3)); // apr
                            lineAccountSB.append(String.format("%-15s", 1)); // collateral_type
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(50000))); // collateral_value
                            lineAccountSB.append(String.format("%-15s", new Random().nextInt(500))); // min_payment
                            lineAccountSB.append(String.format("%-15s", balance)); // ending_balance
                            int totalCreditLine = new Random().nextInt(5000)+balance;
                            lineAccountSB.append(String.format("%-15s", totalCreditLine)); // total_credit_line
                            lineAccountSB.append(String.format("%-15s", totalCreditLine - balance)); // available_credit
                            lineAccountSB.append(String.format("%-15s", 0)); // days_past_due
                            lineAccountSB.append(String.format("%-15s", 0)); // past_due_YTD
                            lineAccountSB.append(String.format("%-15s", 0)); // past_due_LTD
                            lineAccountSB.append(String.format("%-15s", 0)); // in_collections
                            lineAccountsWriter.print(lineAccountSB.toString() + "\n");

                            // Line (Card) Transactions
                            for (int j = 0; j < 10; j++) {
                                StringBuffer lineTransactionSB = new StringBuffer();
                                uaid = Generator.adic("ksp", ucid + new Random().nextInt(1000));
                                lineTransactionSB.append(String.format("%-50s", flattened==1?ucid:"")); // ucid
                                lineTransactionSB.append(String.format("%-50s", ucid)); // uhid
                                lineTransactionSB.append(String.format("%-50s", uaid)); // uaid
                                lineTransactionSB.append(String.format("%-15s", "902")); // reltype
                                lineTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(transStartDate.getTime()))); // date
                                cloned = (Calendar) transStartDate.clone();
                                cloned.add(Calendar.MINUTE, new Random().nextInt(60 * 23));
                                lineTransactionSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // post_date
                                lineTransactionSB.append(String.format("%-15s", "500")); // type
                                lineTransactionSB.append(String.format("%-15s", "US")); // mrkting_code
                                lineTransactionSB.append(String.format("%-15s", "Posted")); // status
                                lineTransactionSB.append(String.format("%-15s", new Random().nextInt(999) + 1)); // amount
                                lineTransactionSB.append(String.format("%-15s", new Random().nextInt(8999) + 1000)); // zip_code
                                lineTransactionSB.append(String.format("%-15s", "100")); // merchant_id
                                lineTransactionSB.append(String.format("%-15s", "8042")); // sic_code
                                lineTransactionSB.append(String.format("%-15s", "50773")); // naics_code
                                lineTransactionSB.append(String.format("%-15s", "5969589")); // mcc
                                lineTransactionSB.append(String.format("%-15s", "1")); // intrchg_rate
                                lineTransactionSB.append(String.format("%-15s", "USD")); // currency_id
                                lineTransactionSB.append(String.format("%-15s", "1")); // currency_conv_rate
                                lineTransactionSB.append(String.format("%-300s", termcodeLeafs.get(new Random().nextInt(29)))); // payee
                                lineTransactionSB.append(String.format("%-300s", "PORT HADLOCK VISION CL")); // memo
                                cardTransactionsWriter.print(lineTransactionSB.toString() + "\n");
                                transStartDate.add(Calendar.MILLISECOND,100);
                            }
                            break;
                        }
                    }
                }
                startDate.add(Calendar.MILLISECOND, 1); // If batch, increment by the number of milliseconds proportional to two years and the population
                if (i1 % 10000 == 0)
                {
                    System.out.print("."); // Show activity to console user
                }
            }

            customersWriter.close();

            //            interactionsWriter.close();
            //
            //            applicationsWriter.close();

            cardAccountsWriter.close();
            depositAccountsWriter.close();
            insuranceAccountsWriter.close();
            loanAccountsWriter.close();
            lineAccountsWriter.close();

            cardTransactionsWriter.close();
            depositTransactionsWriter.close();
            insuranceTransactionsWriter.close();
            loanTransactionsWriter.close();

            // TODO replace w/ SLF4J
            System.out.println("\n" + numberOfCustomers + " created.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

