package com.myapp;

import static android.content.Context.CONSUMER_IR_SERVICE;

import android.hardware.ConsumerIrManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.lang.reflect.Array;

public class InfraredModule extends ReactContextBaseJavaModule {
    ReactApplicationContext context;
    ConsumerIrManager mCIR;
    public InfraredModule(@Nullable ReactApplicationContext reactContext ){
        super(reactContext);
        context = reactContext;
        mCIR = (ConsumerIrManager)context.getSystemService(CONSUMER_IR_SERVICE);
    }
    @NonNull
    @Override
    public String getName() {
        return  "Test";
    }
    @ReactMethod
    public void test(String name, Callback callback) {
        try {

            callback.invoke(null, mCIR.hasIrEmitter());

        } catch (Exception e) {
            callback.invoke(e, null);
        }
    }
    @ReactMethod
    public void trasmit(String name, Callback callback) {
        try {
            int[] pattern = {1901, 4453, 625, 1614, 625, 1588, 625, 1614, 625, 442, 625, 442, 625,
                    468, 625, 442, 625, 494, 572, 1614, 625, 1588, 625, 1614, 625, 494, 572, 442, 651,
                    442, 625, 442, 625, 442, 625, 1614, 625, 1588, 651, 1588, 625, 442, 625, 494, 598,
                    442, 625, 442, 625, 520, 572, 442, 625, 442, 625, 442, 651, 1588, 625, 1614, 625,
                    1588, 625, 1614, 625, 1588, 625, 48958};
            mCIR.transmit(38400, pattern);
            callback.invoke(null, true);

        } catch (Exception e) {
            callback.invoke(e, false);
        }
    }
    @ReactMethod
    public void range(String name, Callback callback) {
        try {
            StringBuilder b = new StringBuilder();
            ConsumerIrManager.CarrierFrequencyRange[] freqs = mCIR.getCarrierFrequencies();
            b.append("IR Carrier Frequencies: ");
            for (ConsumerIrManager.CarrierFrequencyRange range : freqs) {
                b.append(String.format("    %d - %d\n", range.getMinFrequency(),
                        range.getMaxFrequency()));
            }
            callback.invoke(null, b.toString());
        } catch (Exception e) {
            callback.invoke(e, "error");
        }
    }

    @ReactMethod
    public void transmitProntoCode(String prontoHexCode, Callback callback) {
        String[] codeParts = prontoHexCode.split(" ");

        int prontoClockFrequency = Integer.parseInt(codeParts[1], 16);
        Double exactCarrierFrequency = 1000000/(prontoClockFrequency * 0.241246);

        int carrierFrequency = exactCarrierFrequency.intValue();
        int firstSequenceBurstPairs = Integer.parseInt(codeParts[2], 16);
        int secondSequenceBurstPairs = Integer.parseInt(codeParts[3], 16);
        int[] pattern = new int[(firstSequenceBurstPairs * 2) + (secondSequenceBurstPairs * 2)];

        int i = 0;
        int firstPairIndex = 4;
        int secondPairIndex = firstPairIndex + (firstSequenceBurstPairs * 2);

        for (int j = firstPairIndex; j < secondPairIndex; i++, j++) {
            pattern[i] = Integer.parseInt(codeParts[j], 16) * (1000000 / carrierFrequency);
        }

        for (int j = secondPairIndex; j < secondPairIndex + (secondSequenceBurstPairs * 2); i++, j++) {
            pattern[i] = Integer.parseInt(codeParts[j], 16) * (1000000 / carrierFrequency);
        }

        try {
            mCIR.transmit(carrierFrequency, pattern);
            callback.invoke(null, true);
        } catch (Exception e) {
            callback.invoke(null, false);
        }
    }
}