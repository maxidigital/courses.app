/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.sheets.medical;

import blue.underwater.commons.datetime.XDate;

/**
 * Represents a medical questionnaire form submitted by a participant
 *
 * @author maxi
 */
public class MedicalForm {
    private XDate timestamp;
    private String email;
    private boolean hasLungOrHeartIssues;
    private boolean hasEyeEarSinusIssues;
    private boolean hasHeadInjuryHistory;
    private boolean hasPsychologicalIssues;
    private boolean hasRecentSurgery;
    private boolean hasBackProblems;
    private boolean hasRecentStomachIssues;
    private boolean isTakingMedications;
    
    public MedicalForm(XDate timestamp, String email, boolean hasLungOrHeartIssues, 
            boolean hasEyeEarSinusIssues, boolean hasHeadInjuryHistory, 
            boolean hasPsychologicalIssues, boolean hasRecentSurgery, 
            boolean hasBackProblems, boolean hasRecentStomachIssues, 
            boolean isTakingMedications) {
        this.timestamp = timestamp;
        this.email = email;
        this.hasLungOrHeartIssues = hasLungOrHeartIssues;
        this.hasEyeEarSinusIssues = hasEyeEarSinusIssues;
        this.hasHeadInjuryHistory = hasHeadInjuryHistory;
        this.hasPsychologicalIssues = hasPsychologicalIssues;
        this.hasRecentSurgery = hasRecentSurgery;
        this.hasBackProblems = hasBackProblems;
        this.hasRecentStomachIssues = hasRecentStomachIssues;
        this.isTakingMedications = isTakingMedications;
    }
    
    public XDate getTimestamp() {
        return timestamp;
    }
    
    public String getEmail() {
        return email;
    }
    
    public boolean hasLungOrHeartIssues() {
        return hasLungOrHeartIssues;
    }
    
    public boolean hasEyeEarSinusIssues() {
        return hasEyeEarSinusIssues;
    }
    
    public boolean hasHeadInjuryHistory() {
        return hasHeadInjuryHistory;
    }
    
    public boolean hasPsychologicalIssues() {
        return hasPsychologicalIssues;
    }
    
    public boolean hasRecentSurgery() {
        return hasRecentSurgery;
    }
    
    public boolean hasBackProblems() {
        return hasBackProblems;
    }
    
    public boolean hasRecentStomachIssues() {
        return hasRecentStomachIssues;
    }
    
    public boolean isTakingMedications() {
        return isTakingMedications;
    }
    
    /**
     * Checks if there are any medical issues that might need attention
     * 
     * @return true if any medical question was answered affirmatively
     */
    public boolean hasAnyMedicalIssues() {
        return hasLungOrHeartIssues || hasEyeEarSinusIssues || hasHeadInjuryHistory || 
               hasPsychologicalIssues || hasRecentSurgery || hasBackProblems || 
               hasRecentStomachIssues || isTakingMedications;
    }
    
    /**
     * Returns an array of MedicalFormQuestion enums that represent the medical issues 
     * this participant has reported.
     * 
     * @return Array of MedicalFormQuestion for issues with affirmative answers
     */
    public MedicalFormQuestion[] getMedicalIssues() {
        java.util.List<MedicalFormQuestion> issues = new java.util.ArrayList<>();
        
        if (hasLungOrHeartIssues) issues.add(MedicalFormQuestion.LUNGS_HEART_CIRCULATION);
        if (hasEyeEarSinusIssues) issues.add(MedicalFormQuestion.EYES_EARS_SINUSES);
        if (hasHeadInjuryHistory) issues.add(MedicalFormQuestion.NEUROLOGIC_HISTORY);
        if (hasPsychologicalIssues) issues.add(MedicalFormQuestion.PSYCHOLOGICAL_TREATMENT);
        if (hasRecentSurgery) issues.add(MedicalFormQuestion.RECENT_SURGERY);
        if (hasBackProblems) issues.add(MedicalFormQuestion.BACK_HERNIA_ULCERS_DIABETES);
        if (hasRecentStomachIssues) issues.add(MedicalFormQuestion.STOMACH_INTESTINAL_PROBLEMS);
        if (isTakingMedications) issues.add(MedicalFormQuestion.PRESCRIPTION_MEDICATIONS);
        
        return issues.toArray(new MedicalFormQuestion[0]);
    }
    
    /**
     * Formats the medical form information for a Telegram message
     * Reports any health issues or indicates if participant is in good health
     * 
     * @return A formatted string in HTML format suitable for sending as a Telegram message
     */
    public String toTelegramMessage() {
        StringBuilder sb = new StringBuilder();
        
        // Header with email
        sb.append("<b>Medical Form</b> for: <code>").append(email).append("</code>\n\n");
        
        // Check if there are any medical issues
        if (!hasAnyMedicalIssues()) {
            sb.append("✅ <b>Participant is in good health</b> - No medical issues reported.\n");
            sb.append("All questions were answered negatively.");
        } else {
            sb.append("⚠️ <b>Medical attention needed</b> - Participant has reported the following:\n\n");
            
            // List all the medical issues
            if (hasLungOrHeartIssues) {
                sb.append("• 🫁 <b>Lung/Heart/Circulatory Issues</b>: Yes\n");
            }
            
            if (hasEyeEarSinusIssues) {
                sb.append("• 👁️👂 <b>Eye/Ear/Sinus Issues</b>: Yes\n");
            }
            
            if (hasHeadInjuryHistory) {
                sb.append("• 🧠 <b>Head Injuries/Neurological Conditions</b>: Yes\n");
            }
            
            if (hasPsychologicalIssues) {
                sb.append("• 🧠 <b>Psychological Issues</b>: Yes\n");
            }
            
            if (hasRecentSurgery) {
                sb.append("• 🏥 <b>Recent Surgery</b>: Yes\n");
            }
            
            if (hasBackProblems) {
                sb.append("• 🦴 <b>Back Problems/Hernia/Diabetes</b>: Yes\n");
            }
            
            if (hasRecentStomachIssues) {
                sb.append("• 🦠 <b>Recent Stomach/Intestinal Problems</b>: Yes\n");
            }
            
            if (isTakingMedications) {
                sb.append("• 💊 <b>Taking Prescription Medications</b>: Yes\n");
            }
            
            sb.append("\n⚠️ <b>Further assessment recommended</b> before freediving activities.");
        }
        
        // Add submission timestamp in a human-readable format
        if (timestamp != null) {
            sb.append("\n\n<i>Form submitted on: ").append(timestamp.format("dd/MM/yyyy HH:mm")).append("</i>");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "MedicalForm{email=" + email + "}";
    }
}
