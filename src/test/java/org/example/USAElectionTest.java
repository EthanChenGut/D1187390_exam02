package org.example;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class USAElectionTest {

    @Test
    void getElectionResult() {
        List<Object[]> data = new ArrayList<>();
        List<Object[]> expectance = new ArrayList<>();

        try (InputStream is = USAElectionTest.class.getResourceAsStream("/votes2020.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;

            while ((line = br.readLine()) != null) {
                // 移除引號並分割每行的欄位
                String[] values = line.split(",");

                // 將資料轉為 Object[]
                Object[] col = new Object[4];
                col[0] = values[0]; // 州名 (String)
                col[1] = values[1]; // 選舉人票數
                col[2] = values[2]; // 一號候選人 得票數
                col[3] = values[3]; // 二號候選人 得票數

                Object[] result = new Object[3];
                result[0] = values[4];
                result[1] = values[5];
                result[2] = values[6];

                data.add(col);
                expectance.add(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(data.get(1));
        List<Object[]> actual = USAElection.getElectionResult(data);

        int i=0;
        for(Object[] fields : expectance){
            if(((String) fields[0]).equals("Winner")){
                continue;
            }

            System.out.println("Expectance: " + Arrays.toString(fields));  // 輸出 expectance 的資料
            System.out.println("Actual: " + Arrays.toString(actual.get(i)));  // 輸出 actual 的資料

            assertArrayEquals(fields, actual.get(i));
            i++;
        }

    }
}