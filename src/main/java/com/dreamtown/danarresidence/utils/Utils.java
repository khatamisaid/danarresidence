package com.dreamtown.danarresidence.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dreamtown.danarresidence.repository.LogAktivitasRepository;

@Service
public class Utils {

    @Autowired
    private LogAktivitasRepository logAktivitasRepository;

    public Map logAktivitas() {
        Map res = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String tglDari = simpleDateFormat.format(new Date());
        String tglSampai = simpleDateFormat.format(c.getTime());
        res.put("totalKeseluruhan", logAktivitasRepository.findAll().size());
        res.put("totalHariIni", logAktivitasRepository.logHariBetween(tglDari,
                tglSampai));
        return res;
    }

    public String[] checkNullOrEmptyString(String word) {
        try {
            if (word.trim().equalsIgnoreCase("")) {
                return null;
            } else {
                if (word.trim().contains("\n")) {
                    return word.split("\n");
                } else {
                    return new String[] { word.trim() };
                }
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Integer getRandomIndex(Integer length) {
        Random rand = new Random();
        return rand.nextInt(length);
    }

    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
