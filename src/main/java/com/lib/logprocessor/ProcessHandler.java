package com.lib.logprocessor;

import org.junit.Test;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by sekarayukarindra on 29/02/20.
 */
public class ProcessHandler {

    public ProcessHandler(){}

    private Map<Integer, Process> storeExecutionTimeForServices(String logPath){
        Map<Integer, Process> logMap = new HashMap<>();
        String line, time, processName, processId;
        int lineAt=0;

        try {
            FileReader fileReader = new FileReader(logPath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                lineAt++;
                processName = line.substring(line.lastIndexOf("("));
                processName = processName.replaceAll("[:0-9()]", "");
                processId = line.substring(line.lastIndexOf("("));
                processId = processId.replaceAll("[:a-zA-Z()]", "");
                time = line.substring(0, line.indexOf(" "));

                Process process = new Process(processName, Integer.parseInt(processId), time);
                logMap.put(lineAt, process);
            }
            fileReader.close();
            bufferedReader.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        List<Process> processById = new ArrayList<>(logMap.values());
        Collections.sort(processById);
        Map<Integer, Process> logMapExec = new HashMap<>();

        for(int i=0;i<processById.size();i++){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss,SSS");
            Date start, end;
            String pname;
            String pstart;
            String pend;
            int pid;

            long diffInMillies = 0;
            int j=i+1;

            if(j!=processById.size()) {
                if (processById.get(i).getProcessId() == processById.get(j).getProcessId()) {
                    pname = processById.get(i).getProcessName();
                    pid = processById.get(i).getProcessId();
                    pstart = processById.get(i).getTimeProcess();
                    pend = processById.get(j).getTimeProcess();

                    try {
                        start = simpleDateFormat.parse(pstart);
                        end = simpleDateFormat.parse(pend);
                        diffInMillies = Math.abs(end.getTime() - start.getTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (i != processById.size() - 2) {
                        i = j;
                    }

                    Process process2 = new Process(pname, pid, diffInMillies);
                    logMapExec.put(process2.getProcessId(), process2);
                }
            }else {
                break;
            }
        }
        return logMapExec;
    }

    public void processLog(String sourceLog, String destinationLog){
        Map<Integer, Process> logMap = storeExecutionTimeForServices(sourceLog);
        List<Process> listMap2 = new ArrayList<>(logMap.values());

        List<List<Process>> groupedProcess = null;
        for(int x=0;x<listMap2.size();x++){
            groupedProcess = listMap2.stream().collect(Collectors.collectingAndThen(Collectors.groupingBy(o -> o.getProcessName()),
                    m -> new ArrayList<>(m.values())));

        }

        String procname;
        int procsize;
        long max;
        long maxTime;
        try {
            FileWriter fileWriter = new FileWriter(destinationLog);
            PrintWriter writer = new PrintWriter(fileWriter);
            for (List<Process> groupedProces : groupedProcess) {
                procname = groupedProces.get(0).getProcessName();
                procsize = groupedProces.size();
                max = groupedProces.stream()
                        .mapToLong(x -> x.getExecutionTime())
                        .max().getAsLong();
                maxTime = TimeUnit.MILLISECONDS.toHours(max);
                writer.println("Service Name : " + procname);
                writer.println("Amount of request to service : " + procsize);
                writer.println("Max execution time in miliseconds : " + max + " miliseconds");
                writer.println("Max execution time in hours : "+maxTime+" hours");
            }
            writer.close();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void testProcess(){
        processLog(System.getProperty("user.dir")+"/test.log", System.getProperty("user.dir")+"/result.txt");
    }



}
