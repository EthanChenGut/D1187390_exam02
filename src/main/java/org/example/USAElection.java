package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

class USAElection {

    // 自訂例外類別
    static class InvalidElectoralVotesException extends RuntimeException {
        public InvalidElectoralVotesException(String message) {
            super(message);
        }
    }

    public static List<Object[]> getElectionResult(List<Object[]> electionData) {
        String winner = "NONE";
        String AlignOrSplit = "X";
        // 每一州的資料用一個 HashMap 來表示
        // key 是州的名稱, value 是一陣列，包含該州選舉人票數、兩個候選人的普選人口得票數
        Map<String, int[]> state_votes = new LinkedHashMap<>();
        String candidate1="", candidate2="";

        if (electionData == null || electionData.isEmpty()) {
            throw new IllegalArgumentException("Election data ERROR.");
        }

        for (Object[] fields : electionData) {
            if (((String) fields[0]).equals("State")) {
                candidate1 = (String) fields[2];
                candidate2 = (String) fields[3];
                continue;
            }

            int[] v = {
                    Integer.parseInt((String) fields[1]),
                    Integer.parseInt((String) fields[2]),
                    Integer.parseInt((String) fields[3])
            };
            state_votes.put((String) fields[0], v);
        }
        List<Object[]> result = new ArrayList<>();
        int c1_popular_votes = 0; // 一號候選人的人數票
        int c2_popular_votes = 0; // 二號候選人的人數票
        int c1_electoral_votes = 0; // 一號候選人的選舉人票
        int c2_electoral_votes = 0; // 二號候選人的選舉人票
        int winner_electoral_votes = 0;

        for (Map.Entry<String, int[]> voteEntry : state_votes.entrySet()) {
            String state = voteEntry.getKey();
            int[] vote_arr = voteEntry.getValue();
            Object[] row = new Object[3];

            if(state.equals("Nebraska")||state.equals("Maine")){
                double c1_rate=(double) vote_arr[1]/(vote_arr[1]+vote_arr[2]);
                double c2_rate=(double) vote_arr[2]/(vote_arr[1]+vote_arr[2]);

                int c1_votes=(int) Math.round(c1_rate*vote_arr[0]);
                int c2_votes=(int) Math.round(c2_rate*vote_arr[0]);

                if(c1_votes>c2_votes){
                    row = new Object[]{"Biden", String.valueOf(c1_votes), "Align"};
                }else if(c1_votes<c2_votes){
                    row = new Object[]{"Trump", String.valueOf(c2_votes), "Align"};
                }else{
                    row = new Object[]{"NONE","-1","X"};
                }

                c1_electoral_votes += c1_votes;
                c2_electoral_votes += c2_votes;

            }else{
                if(vote_arr[1]>vote_arr[2]) {
                    row = new Object[]{"Biden", String.valueOf(vote_arr[0]), "Align"};
                }
                else if(vote_arr[1]<vote_arr[2]){
                    row = new Object[]{"Trump", String.valueOf(vote_arr[0]), "Align"};
                }

                if (vote_arr[1] > vote_arr[2]) // 選舉人票
                    c1_electoral_votes += vote_arr[0];
                else if (vote_arr[2] > vote_arr[1])
                    c2_electoral_votes += vote_arr[0];
            }
            result.add(row);

            c1_popular_votes += vote_arr[1]; // 累加票數
            c2_popular_votes += vote_arr[2];

        }

        if (c1_electoral_votes + c2_electoral_votes != 538) {
            throw new InvalidElectoralVotesException("Invalid total electoral votes: " + c1_electoral_votes + c2_electoral_votes);
        }

        if (c1_electoral_votes > c2_electoral_votes) {
            winner = candidate1;
            winner_electoral_votes = c1_electoral_votes;
            AlignOrSplit = (c1_popular_votes > c2_popular_votes) ? "Align" : "Split";
        } else if (c2_electoral_votes > c1_electoral_votes) {
            winner = candidate2;
            winner_electoral_votes = c2_electoral_votes;
            AlignOrSplit = (c2_popular_votes > c1_popular_votes) ? "Align" : "Split";
        } else
            winner_electoral_votes = -1;

        result.add(new Object[]{winner, String.valueOf(winner_electoral_votes), AlignOrSplit});
        return result;
    }
}