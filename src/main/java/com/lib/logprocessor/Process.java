package com.lib.logprocessor;

/**
 * Created by sekarayukarindra on 29/02/20.
 */
public class Process implements Comparable<Process> {

    private String processName;
    private int processId;
    private String timeProcess;
    private long executionTime;

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getTimeProcess() {
        return timeProcess;
    }

    public void setTimeProcess(String timeProcess) {
        this.timeProcess = timeProcess;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public Process(String processName, int processId, String timeProcess){
        setProcessName(processName);
        setProcessId(processId);
        setTimeProcess(timeProcess);
    }

    public Process(String processName, int processId, long executionTime){
        setProcessName(processName);
        setProcessId(processId);
        setExecutionTime(executionTime);
    }

    // override equals and hashCode
    @Override
    public int compareTo(Process process) {
        return (int)(this.processId - process.getProcessId());
    }

}
