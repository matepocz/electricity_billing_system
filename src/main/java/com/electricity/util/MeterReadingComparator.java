package com.electricity.util;

import com.electricity.model.MeterReading;

import java.util.Comparator;

public class MeterReadingComparator implements Comparator<MeterReading> {

    @Override
    public int compare(MeterReading meterReading, MeterReading secondReading) {
        return Long.compare(meterReading.getId(), secondReading.getId());
    }
}
