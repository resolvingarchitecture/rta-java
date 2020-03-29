package ra.rta.rfm.conspref.utilities;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class IndividualFileGenerator {

    public static final String INDIVIDUAL_REGISTRATION_FIXED_WIDTH_COMMAND_PATH_JSON = "individual_registration_fixed_width";

    @SuppressWarnings("resource")
    public static void main(String[] args) {

        try {
            String stagingFolder = args[0];
            int numberOfIndividuals = Integer.parseInt(args[1]);
            int numberOfDays = Integer.parseInt(args[2]);
            int flattened = Integer.parseInt(args[3]);

            System.out.println("Creating " + numberOfIndividuals
                    + " Inviduals with randomized Transactions over " + numberOfDays + " number of days "
                    + (flattened == 1 ? "flattened" : "relational") + "...");

            List<String> termcodeLeafs = new ArrayList<>(30);
            // Non-Competitive
            termcodeLeafs.add("HOTEL OH US");
            termcodeLeafs.add("MOTEL NV USA89014");
            termcodeLeafs.add("TARGET T2087");
            // Competitive
            termcodeLeafs.add("BMO HARRIS BANK NA MOORESVILLE IN");
            termcodeLeafs.add("REDBRAND CREDIT UNION VISA");


            File individualsFile = new File(stagingFolder + INDIVIDUAL_REGISTRATION_FIXED_WIDTH_COMMAND_PATH_JSON +"/"+ INDIVIDUAL_REGISTRATION_FIXED_WIDTH_COMMAND_PATH_JSON +".txt");
            if (!individualsFile.exists()) {
                individualsFile.createNewFile();
            }
            PrintWriter individualsWriter = new PrintWriter(new BufferedWriter(new FileWriter(individualsFile)));

            //            File interactionsFile = new File(stagingFolder + Interaction.CUSTOMER_INTERACTION_FIXED_WIDTH_COMMAND_PATH_JSON +"/"+Interaction.CUSTOMER_INTERACTION_FIXED_WIDTH_COMMAND_PATH_JSON+".txt");
            //            if(!interactionsFile.exists()) interactionsFile.createNewFile();
            //            PrintWriter interactionsWriter = new PrintWriter(new BufferedWriter(new FileWriter(interactionsFile)));

            File transactionsFile = new File(stagingFolder + "financial_transaction_fixed_width/financial_transaction_fixed_width.txt");
            if (!transactionsFile.exists()) {
                transactionsFile.createNewFile();
            }
            PrintWriter transactionsWriter = new PrintWriter(new BufferedWriter(new FileWriter(transactionsFile)));

            String groupName = "g1";
            Calendar today = Calendar.getInstance();
            Calendar endDate = (Calendar)today.clone();
            endDate.add(Calendar.DATE,-1); // yesterday
            Calendar startDate = (Calendar)endDate.clone();
            startDate.add(Calendar.DATE, numberOfDays); // Go back number of days from yesterday
            Calendar transStartDate = (Calendar)startDate.clone();
            Calendar cloned;

            for (int i1 = 0; i1 < numberOfIndividuals; i1++) {
                long id = RandomUtil.nextRandomLong();

                // Individual
                StringBuffer iBuff = new StringBuffer();
                iBuff.append(String.format("%-15s", id)); // id
                Calendar indOpenDate = (Calendar)endDate.clone();
                indOpenDate.add(Calendar.DATE, -new Random().nextInt(numberOfDays));
                iBuff.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(indOpenDate.getTime()))); // open_date;
                iBuff.append(String.format("%-15s", "9999-12-31")); // close_date;
                iBuff.append(String.format("%-15s", "0")); // status;
                iBuff.append(String.format("%-15s", "USA")); // country;
                iBuff.append(String.format("%-15s", "98337")); // zip_code;
                iBuff.append(String.format("%-15s", new Random().nextInt(2010-1900)+1900)); // birth_year;
                iBuff.append(String.format("%-15s", "0")); // deceased_year;
                iBuff.append(String.format("%-15s", "0")); // type;
                iBuff.append(String.format("%-15s", "WA")); // state;
                iBuff.append(String.format("%-15s", new Random().nextInt(10000000))); // investable_assets;
                iBuff.append(String.format("%-15s", "Y")); // estatement_indicator;
                iBuff.append(String.format("%-15s", new Random().nextInt(1))); // glba_opt_out;
                iBuff.append(String.format("%-15s", new Random().nextInt(1))); // solicitation_code;
                iBuff.append(String.format("%-15s", "100")); // segmentation;
                iBuff.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime()))); // business_inception_date;
                iBuff.append(String.format("%-15s", "100")); // business_sic_code;
                iBuff.append(String.format("%-15s", "0")); // business_naics_code;
                iBuff.append(String.format("%-15s", new Random().nextInt(100000000))); // business_annual_sales;
                iBuff.append(String.format("%-15s", new Random().nextInt(998)+1)); // business_num_employees;
                individualsWriter.print(iBuff.toString() + "\n");

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

                // Financial Transactions
                int numberDepositTransactions = 1; // Bi-Weekly deposits
                if(numberOfDays > 14) {
                    numberDepositTransactions = Math.round(numberOfDays / 14);
                }
                for(int i2=0; i2 < numberDepositTransactions; i2++) {
                    Calendar individualTransStartDate = (Calendar) indOpenDate.clone();
                    StringBuffer transSB = new StringBuffer();
                    transSB.append(String.format("%-15s", id)); // id
                    transSB.append(String.format("%-15s", "902")); // reltype
                    transSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(individualTransStartDate.getTime()))); // date
                    cloned = (Calendar) individualTransStartDate.clone();
                    cloned.add(Calendar.MINUTE, new Random().nextInt(60 * 23));
                    transSB.append(String.format("%-15s", new SimpleDateFormat("yyyy-MM-dd").format(cloned.getTime()))); // post_date
                    transSB.append(String.format("%-15s", "500")); // type
                    transSB.append(String.format("%-15s", "US")); // mrkting_code
                    transSB.append(String.format("%-15s", "Posted")); // status
                    transSB.append(String.format("%-15s", new Random().nextInt(999) + 1)); // amount
                    transSB.append(String.format("%-15s", new Random().nextInt(8999) + 1000)); // zip_code
                    transSB.append(String.format("%-15s", "100")); // merchant_id
                    transSB.append(String.format("%-15s", "8042")); // sic_code
                    transSB.append(String.format("%-15s", "50773")); // naics_code
                    transSB.append(String.format("%-15s", "5969589")); // mcc
                    transSB.append(String.format("%-15s", "1")); // intrchg_rate
                    transSB.append(String.format("%-15s", "USD")); // currency_id
                    transSB.append(String.format("%-15s", "1")); // currency_conv_rate
                    transSB.append(String.format("%-300s", termcodeLeafs.get(new Random().nextInt(29)))); // payee
                    transSB.append(String.format("%-300s", "PORT HADLOCK VISION CL")); // memo
                    transactionsWriter.print(transSB.toString() + "\n");
                }

                startDate.add(Calendar.MILLISECOND, 1); // If batch, increment by the number of milliseconds proportional to two years and the population
                if (i1 % 10000 == 0)
                {
                    System.out.print("."); // Show activity to console user
                }
            }

            individualsWriter.close();
            //            interactionsWriter.close();
            transactionsWriter.close();

            System.out.println("\n" + numberOfIndividuals + " created.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

