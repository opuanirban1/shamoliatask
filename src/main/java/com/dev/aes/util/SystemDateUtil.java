package com.dev.aes.util;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

@Service
@Data
public class SystemDateUtil {
    private static final Logger logger = Logger.getLogger(SystemDateUtil.class.getName());

   /* private static final String[] allformat = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "EEE MMM dd HH:mm:ss zzz yyyy",
            "dd MMM yyyy",
            "dd MMM yyyy",
            "dd-MMM-yy",
            "dd/MM/yyyy",
            "MMMM dd, yyyy",
            "dd MMMM yyyy",
            "dd MMMM, yyyy",
            "dd-MM-yy",
            "dd-MM-yyyy",
            "MM/yyyy",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",   "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "MM/dd/yyyy'T'HH:mm:ssZ",     "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss",        "yyyyMMdd",
            "dd/MM/yyyy"

    };*/

    private static final String[] allformat = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "EEE MMM dd HH:mm:ss zzz yyyy",
            "dd MMM yyyy",
            "dd MMM yyyy",
            "dd-MMM-yy",
            "dd/MM/yyyy",
            "MMMM dd, yyyy",
            "dd MMMM yyyy",
            "dd MMMM, yyyy",
            "dd-MM-yy",
            "dd-MM-yyyy",
            "MM/yyyy",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",   "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "MM/dd/yyyy HH:mm:ss",        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'",
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS",
            "MM/dd/yyyy'T'HH:mm:ssZ",     "MM/dd/yyyy'T'HH:mm:ss",
            "yyyy:MM:dd HH:mm:ss",        "yyyyMMdd",
            "dd/MM/yyyy",
            "yyyyMMddZ ",
            "yyyyMMdd ",
            "yyyy-MM-dd G ",
            "yyyy-MM-ddXXX ",
            "dd.MM.yyyy",
            /*"yyyy-MM-dd'T'HH:mm:ss.SSS'['VV']' ",
            "yyyy-MM-dd'T'HH:mm:ss,SSS'['VV']' ",
            "yyyy-MM-dd HH:mm:ss.SSS'['VV']' ",
            "yyyy-MM-dd HH:mm:ss,SSS'['VV']' ",*/
            "yyyy-MM-dd'T'HH:mm:ss.SSS ",
            "yyyy-MM-dd'T'HH:mm:ss,SSS ",
            "yyyy-MM-dd HH:mm:ss.SSS ",
            "yyyy-MM-dd HH:mm:ss,SSS ",
            "yyyy-MM-dd'T'HH:mm:ss ",
            "yyyy-MM-dd'T'HH:mm:ss'Z' ",
            "yyyy-MM-dd'T'HH:mm:ssZ ",
            "yyyy-MM-dd HH:mm:ss'Z' ",
            "yyyy-MM-dd HH:mm:ssZ ",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z' ",
            "yyyy-MM-dd'T'HH:mm:ss,SSS'Z' ",
            "yyyy-MM-dd HH:mm:ss.SSS'Z' ",
            "yyyy-MM-dd HH:mm:ss,SSS'Z' ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX ",
            "yyyy-MM-dd'T'HH:mm:ss,SSSXXX ",
            "yyyy-MM-dd HH:mm:ss.SSSXXX ",
            "yyyy-MM-dd HH:mm:ss,SSSXXX ",
            "yyyy-MM-dd'T'HH:mm:ssX ",
            "yyyy-MM-dd HH:mm:ssX ",
            "yyyy-MM-dd'T'HH:mm:ssXXX ",
            "yyyy-MM-dd HH:mm:ssXXX ",
            "yyyy-DDDXXX ",
            /* "YYYY'W'wc ",
             "YYYY-'W'w-c ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']' ",
             "yyyy-MM-dd'T'HH:mm:ss,SSSXXX'['VV']' ",
             "yyyy-MM-dd HH:mm:ss.SSSXXX'['VV']' ",
             "yyyy-MM-dd HH:mm:ss,SSSXXX'['VV']' ",
             "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']' ",
             "yyyy-MM-dd HH:mm:ssXXX'['VV']' ",*/
            "d.M.yy ",
            "d.M.yy H.mm ",
            "d.M.yyyy H.mm.ss ",
            "d.M.yyyy H:mm:ss ",
            "dd-MM-yy ",
            "dd-MM-yy HH:mm ",
            "dd-MM-yyyy HH:mm:ss ",
            "dd.MM.yy ",
            "d. MMMM yyyy ",
            "EEEE, d. MMMM yyyy ",
            "dd.MM.yyyy ",
            "dd.MM.yy HH:mm ",
            "d. MMMM yyyy HH:mm:ss z ",
            "dd.MM.yyyy HH:mm:ss ",
            "dd.MM.yy HH:mm:ss ",
            "dd.MM.yyyy HH:mm ",
            "EEEE, d. MMMM yyyy HH:mm' Uhr 'z ",
            "d-MMM-yyyy ",
            "dd/MM/yy h:mm a ",
            "EEEE, MMMM d, yyyy h:mm:ss 'o''clock' a z ",
            "d-MMM-yyyy h:mm:ss a ",
            "dd MMMM yyyy ",
            "EEEE, d MMMM yyyy ",
            "dd-MMM-yyyy ",
            "dd MMMM yyyy HH:mm:ss z ",
            "EEEE, d MMMM yyyy HH:mm:ss 'o''clock' z ",
            "dd-MMM-yyyy HH:mm:ss ",
            /*"dd-MMM-yy hh.mm.ss.nnnnnnnnn a ",*/
            "M/d/yy ",
            "MM/dd/yy ",
            "MM-dd-yy ",
            "M-d-yy ",
            "MMM d, yyyy ",
            "MMMM d, yyyy ",
            "EEEE, MMMM d, yyyy ",
            "MMM d yyyy ",
            "MMMM d yyyy ",
            "MM-dd-yyyy ",
            "M-d-yyyy ",
            "yyyy-MM-ddXXX ",
            "dd/MM/yyyy ",
            "d/M/yyyy ",
            "MM/dd/yyyy ",
            "M/d/yyyy ",
            "yyyy/M/d ",
            "M/d/yy h:mm a ",
            "MM/dd/yy h:mm a ",
            "MM-dd-yy h:mm a ",
            "M-d-yy h:mm a ",
            "MMM d, yyyy h:mm:ss a ",
            "EEEE, MMMM d, yyyy h:mm:ss a z ",
            "EEE MMM dd HH:mm:ss z yyyy ",
            "EEE, d MMM yyyy HH:mm:ss Z ",
            "d MMM yyyy HH:mm:ss Z ",
            "MM-dd-yyyy h:mm:ss a ",
            "M-d-yyyy h:mm:ss a ",
            "yyyy-MM-dd h:mm:ss a ",
            "yyyy-M-d h:mm:ss a ",
            "yyyy-MM-dd HH:mm:ss.S ",
            "dd/MM/yyyy h:mm:ss a ",
            "d/M/yyyy h:mm:ss a ",
            "MM/dd/yyyy h:mm:ss a ",
            "M/d/yyyy h:mm:ss a ",
            "MM/dd/yy h:mm:ss a ",
            "MM/dd/yy H:mm:ss ",
            "M/d/yy H:mm:ss ",
            "dd/MM/yyyy h:mm a ",
            "d/M/yyyy h:mm a ",
            "MM/dd/yyyy h:mm a",
            "M/d/yyyy h:mm a ",
            "MM-dd-yy h:mm:ss a ",
            "M-d-yy h:mm:ss a ",
            "MM-dd-yyyy h:mm a ",
            "M-d-yyyy h:mm a ",
            "yyyy-MM-dd h:mm a ",
            "yyyy-M-d h:mm a ",
            "MMM.dd.yyyy ",
            "d/MMM/yyyy H:mm:ss Z ",
            "dd/MMM/yy h:mm a ",
            "d/MM/yy ",
            "d/MM/yy H:mm ",
            "d.M.yy H:mm ",
            "d.MM.yy ",
            "d.MM.yyyy ",
            "d.MM.yy H:mm ",
            "d.MM.yyyy H:mm:ss ",
            "d.M.yyyy ",
            "d.M.yyyy H:mm ",
            "yy-MM-dd ",
            "yyyy-MM-dd ",
            "d MMMM yyyy HH:mm:ss z ",
            "MMMM d, yyyy h:mm:ss z a ",
            "yyyy-MM-dd HH:mm:ss ",
            "EEEE d MMMM yyyy H' h 'mm z ",
            "dd/MM/yy ",
            "d MMM yyyy ",
            "d MMMM yyyy ",
            "EEEE d MMMM yyyy ",
            "dd/MM/yy HH:mm ",
            "MM/dd/yy HH:mm ",
            "M/d/yy HH:mm ",
            "MM-dd-yy HH:mm ",
            "M-d-yy HH:mm ",
            "d MMM yyyy HH:mm:ss ",
            "d MMMM yyyy HH:mm:ss z ",
            "MM-dd-yyyy HH:mm:ss ",
            "M-d-yyyy HH:mm:ss ",
            "yyyy-M-d HH:mm:ss ",
            "dd/MM/yyyy HH:mm:ss ",
            "d/M/yyyy HH:mm:ss ",
            "MM/dd/yyyy HH:mm:ss ",
            "M/d/yyyy HH:mm:ss ",
            "EEEE d MMMM yyyy HH' h 'mm z",
            "dd/MM/yy HH:mm:ss",
            "MM/dd/yy HH:mm:ss ",
            "M/d/yy HH:mm:ss ",
            "dd/MM/yyyy HH:mm ",
            "d/M/yyyy HH:mm ",
            "MM/dd/yyyy HH:mm ",
            "M/d/yyyy HH:mm ",
            "MM-dd-yy HH:mm:ss ",
            "M-d-yy HH:mm:ss ",
            "MM-dd-yyyy HH:mm ",
            "M-d-yyyy HH:mm ",
            "yyyy-M-d HH:mm ",
            "yy/MM/dd HH:mm ",
            "yyyy.MM.dd ",
            "yyyy.MM.dd HH:mm:ss ",
            "yyyy.MM.dd HH:mm ",
            "yyyy.MM.dd. ",
            "yyyy.MM.dd. H:mm:ss ",
            "yyyy.MM.dd. H:mm ",
            "d.M.yyyy HH:mm:ss ",
            "d.M.yyyy HH:mm ",
            "d-MMM-yyyy ",
            "dd/MM/yy H.mm ",
            "yy-MM-dd HH:mm ",
            "d-MMM-yyyy H.mm.ss ",
            "d MMMM yyyy H.mm.ss z ",
            "EEEE d MMMM yyyy H.mm.ss z ",
            "HH:mm dd/MM/yy ",
            "HH:mm:ss dd/MM/yyyy ",
            "yy/MM/dd ",
            "yyyy/MM/dd ",
            "yy/MM/dd H:mm ",
            "MM/dd/yy H:mm ",
            "M/d/yy H:mm ",
            "MM-dd-yy H:mm ",
            "M-d-yy H:mm ",
            "MM-dd-yyyy H:mm:ss ",
            "M-d-yyyy H:mm:ss ",
            "yyyy-MM-dd H:mm:ss ",
            "yy/MM/dd H:mm:ss ",
            "M/d/yy h:mm:ss a ",
            "yyyy/MM/dd H:mm ",
            "dd/MM/yyyy H:mm ",
            "d/M/yyyy H:mm ",
            "MM/dd/yyyy H:mm ",
            "M/d/yyyy H:mm ",
            "MM-dd-yy H:mm:ss ",
            "M-d-yy H:mm:ss ",
            "MM-dd-yyyy H:mm ",
            "M-d-yyyy H:mm ",
            "yyyy-MM-dd H:mm ",
            "yyyy-M-d H:mm ",
            "yy. M. d ",
            "yyyy. M. d ",
            "yy.M.d ",
            "yy.M.d HH.mm ",
            "yyyy-MM-dd HH.mm.ss ",
            "yy.d.M ",
            "yyyy.d.M ",
            "yy.d.M HH:mm ",
            "yyyy.d.M HH:mm:ss ",
            "d.M.yy HH:mm ",
            "d.M.yyyy HH:mm: ",
            "d-M-yy ",
            "d-M-yy H:mm ",
            "dd-MM-yyyy ",
            "dd-MM-yyyy H:mm ",
            "dd.MM.yy H:mm ",
            "dd.MM.yyyy H:mm:ss ",
            "yyyy-MM-dd h:mm:ss.a ",
            "yy-MM-dd h.mm.a ",
            "yyyy-MM-dd h.mm.ss.a z ",
            "d.M.yy. ",
            "dd.MM.yyyy. ",
            "d.M.yy. HH.mm ",
            "dd.MM.yyyy. HH.mm.ss ",
            "dd.MM.yyyy. HH.mm.ss z ",
            "HH:mm:ss dd-MM-yyyy ",
            "HH:mm dd/MM/yyyy ",
            "yy-M-d ",
            "yyyy-M-d ",
            "yyyy'年'M'月'd'日' ",
            "yyyy'年'M'月'd'日' EEEE ",
            "yy-M-d ah:mm ",
            "yyyy-M-d H:mm:ss ",
            "yyyy'年'M'月'd'日' ahh'时'mm'分'ss'秒' ",
            "yyyy'年'M'月'd'日' H'時'mm'分'ss'秒' z ",
            "yyyy'年'M'月'd'日' EEEE ahh'时'mm'分'ss'秒' z "

    };


    public static String parseBanglaOrEnglishDate(String dateString) {
        boolean isBangla = dateString.contains("০") || dateString.contains("১") || dateString.contains("২") ||
                dateString.contains("৩") || dateString.contains("৪") || dateString.contains("৫") ||
                dateString.contains("৬") || dateString.contains("৭") || dateString.contains("৮") ||
                dateString.contains("৯");
        if (isBangla) {
            return banglaToEnglish(dateString);
        } else {
            return convertStringToLongDateValue(dateString);
        }
    }
    public static String convertStringToLongDateValue(String date) {
        DateTimeFormatter[] dateFormats = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
                DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd MMM yyyy"),
                DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd-MM-yy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("MM/yyyy")
        };


        for (DateTimeFormatter dateFormat : dateFormats) {
            try {
                LocalDateTime parsedDateTime;
                if (dateFormat.toString().contains("HH")) {
                    parsedDateTime = LocalDateTime.parse(date, dateFormat);
                } else if (dateFormat.toString().equals("Value(MonthOfYear,2)'/'Value(YearOfEra,4,19,EXCEEDS_PAD)")) {
                    YearMonth yearMonth = YearMonth.parse(date, dateFormat);
                    parsedDateTime = yearMonth.atEndOfMonth().atStartOfDay();
                } else {
                    LocalDate parsedDate = LocalDate.parse(date, dateFormat);
                    parsedDateTime = parsedDate.atStartOfDay();
                }
                return parsedDateTime.toString();
            } catch (Exception e) {
                logger.warning("Failed to parse using format: " + dateFormat.getLocale());
            }
        }
        return null;
    }
    public static String banglaToEnglish(String dateString) {
        dateString = dateString.replace("০", "0")
                .replace("১", "1")
                .replace("২", "2")
                .replace("৩", "3")
                .replace("৪", "4")
                .replace("৫", "5")
                .replace("৬", "6")
                .replace("৭", "7")
                .replace("৮", "8")
                .replace("৯", "9");
        String[] dateStringArr = dateString.split(" ");
        String englishDate = dateStringArr[0] + "/" + getMonth(dateString) + "/" + getYear(dateStringArr);
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy").parse(englishDate);
            return date.toString();
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return null;
    }
    private static int getMonth(String str) {
        int counter = 0;
        String[] banglaMonth = {"জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"};
        for (String s : banglaMonth) {
            counter++;
            if (str.contains(s)) {
                return counter;
            }
        }
        return 0;
    }
    private static int getYear(String[] dateArr) {
        for (String str : dateArr) {
            if (str.matches("[0-9]+") && str.length() == 4) {
                return Integer.parseInt(str);
            }
        }
        return 0;
    }


    public static  String getDateFormatFromInput(String input) {
        if (input != null) {
            for (String parse : allformat) {
                //System.out.println("Anirban "+parse);
                SimpleDateFormat sdf = new SimpleDateFormat(parse);
                try {
                    sdf.parse(input);
                    return parse;
                } catch (ParseException e) {
                }
            }
        }
        return "";
    }// end


    public static String  getNewDateFormatFromUser (String input) throws ParseException{

        try {
            String getFormat = getDateFormatFromInput(input).toString();
            // System.out.println("input " + input + "format" + getFormat);


            boolean isBangla = input.contains("০") || input.contains("১") || input.contains("২") ||
                    input.contains("৩") || input.contains("৪") || input.contains("৫") ||
                    input.contains("৬") || input.contains("৭") || input.contains("৮") ||
                    input.contains("৯");


            if (isBangla) {

                input = input.replace("০", "0")
                        .replace("১", "1")
                        .replace("২", "2")
                        .replace("৩", "3")
                        .replace("৪", "4")
                        .replace("৫", "5")
                        .replace("৬", "6")
                        .replace("৭", "7")
                        .replace("৮", "8")
                        .replace("৯", "9");
            }


            if (getFormat.equals("MM/yyyy")) {

                String getStrdigintmon = input.substring(1, 2);
                String getMonth = getLastDayOfMonthUsingCalendar(Integer.parseInt(getStrdigintmon)).toString();
                String newinput = getMonth + "/" + input;
                String getFormatNew = getDateFormatFromInput(newinput).toString();
                SimpleDateFormat sdf = new SimpleDateFormat(getFormatNew);

                Date inidate = sdf.parse(newinput);
                String specifiedDateString = sdf.format(inidate);
                Long actualTimestamp = sdf.parse(specifiedDateString).getTime();
                //System.out.println("TIme stamp to long"+actualTimestamp);
                SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date dateget = new Date(actualTimestamp);
                //System.out.println("new format "+newsdf.format(dateget));

                return newsdf.format(dateget);
            } else {
                SimpleDateFormat sdf = sdf = new SimpleDateFormat(getFormat);
                ;

                Date inidate = sdf.parse(input);
                String specifiedDateString = sdf.format(inidate);
                Long actualTimestamp = sdf.parse(specifiedDateString).getTime();
                //System.out.println("TIme stamp to long"+actualTimestamp);
                SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date dateget = new Date(actualTimestamp);
                //System.out.println("new format "+newsdf.format(dateget));

                return newsdf.format(dateget);
            }
        }catch (Exception ex){

            return "";
        }

         /*   Date inidate = sdf.parse(input);
            String specifiedDateString = sdf.format(inidate);
            Long actualTimestamp = sdf.parse(specifiedDateString).getTime();
            //System.out.println("TIme stamp to long"+actualTimestamp);
            SimpleDateFormat newsdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date dateget = new Date(actualTimestamp);
            //System.out.println("new format "+newsdf.format(dateget));

        return newsdf.format(dateget);(*/

    }// end function


    public static Integer  getLastDayOfMonthUsingCalendar(Integer  month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Boolean  checkNumericFromString(String input) {
        try {
            Boolean numeric = true;
            numeric = input.matches("-?\\d+(\\.\\d+)?");
            if (numeric) {
                return true;
            } else {
                return false;
            }
        }catch (Exception ex){
            return false;
        }
    }//end function
}