package br.com.alfa11.mspdfmanager.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;

@Slf4j
public class DataUtil {

    public static String getAgoraSomenteData(){
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
        return data;
    }
    public static String getAgoraCompleta(){
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(dataHoraAtual);
        return data;
    }

    public static String getSearchableDate(){
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("yyyyMMddhhmmss").format(dataHoraAtual);
        return data;
    }

    public static int getIdade(String dataNascimento) {

        String dateFormat = "dd/MM/uuuu";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern(dateFormat)
                .withResolverStyle(ResolverStyle.STRICT);
        LocalDate birthdate;
        try {
            birthdate = LocalDate.parse(dataNascimento, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return -1;
        }
        LocalDate currentDate = LocalDate.now();
        int age = calculateAge(birthdate, currentDate);
        return age;
    }

    public static int calculateAge(LocalDate birthdate, LocalDate currentDate) {
        // Calculate period between birthdate and current date
        Period period = Period.between(birthdate, currentDate);

        return period.getYears();
    }

    public static boolean isDataValida(String strDate) {
        String dateFormat = "dd/MM/uuuu";

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern(dateFormat)
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate date = LocalDate.parse(strDate, dateTimeFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean dateIsBeforeNow(String date1) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(date1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date agora = new Date();
        return (date.getTime() < agora.getTime());
    }

    public static boolean dateIsAfterNow(String date1) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(date1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date agora = new Date();
        return (date.getTime() > agora.getTime());
    }

}
