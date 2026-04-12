/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main.sheets.medical;

/**
 *
 * @author maxi
 */
public enum MedicalFormQuestion {
    LUNGS_HEART_CIRCULATION("The person had or currently has problems with lungs/breathing, heart, blood, or circulatory system", "🫁 Lung/Heart/Circulatory Issues"),
    EYES_EARS_SINUSES("The person experienced issues with eyes, ears, or nasal passages/sinuses in the past 12 months", "👁️👂 Eye/Ear/Sinus Issues"),
    NEUROLOGIC_HISTORY("The person has a history of significant head injuries, loss of consciousness, seizures, or persistent neurologic conditions", "🧠 Head Injuries/Neurological Conditions"),
    PSYCHOLOGICAL_TREATMENT("The person is currently undergoing treatment or required treatment within the last five years for psychological problems, including anxiety, panic attacks, or addiction", "🧠 Psychological Issues"),
    RECENT_SURGERY("The person had surgery within the past 12 months or has ongoing problems related to past surgery", "🏥 Recent Surgery"),
    BACK_HERNIA_ULCERS_DIABETES("The person has any back problems, hernia, ulcers, or diabetes", "🦴 Back Problems/Hernia/Diabetes"),
    STOMACH_INTESTINAL_PROBLEMS("The person had recent stomach or intestinal problems, including diarrhea", "🦠 Recent Stomach/Intestinal Problems"),
    PRESCRIPTION_MEDICATIONS("The person is currently taking any prescription medications (except birth control or anti-malarial drugs other than mefloquine/Lariam)", "💊 Taking Prescription Medications");

    private final String description;
    private final String shortDescription;

    MedicalFormQuestion(String description, String shortDescription) {
        this.description = description;
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }
    
    public String getShortDescription() {
        return shortDescription;
    }

    /**
     * Gets the MedicalQuestion by 1-based index (1 to 8).
     * @param index The 1-based index.
     * @return The corresponding MedicalQuestion.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    public static MedicalFormQuestion getByIndex(int index) {
        if (index < 1 || index > values().length) {
            throw new IndexOutOfBoundsException("Index must be between 1 and " + values().length);
        }
        return values()[index - 1];
    }
}
