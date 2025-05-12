package com.rescue.communitytraining.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class TrainingData {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @Column(name = "trainedvolunteer")
    private int trained;
    @Column(name = "education_institutes")
    private int education;
    @Column(name = "religious_institutes")
    private int religious;
    private int other;
    private int rescue_scout;
    private int cert;
    @ManyToOne private District district;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getTrained() { return trained; }
    public void setTrained(int trainedVolunteer) { this.trained = trainedVolunteer; }
    public int getEducation() { return education; }
    public void setEducation(int educationTrainers) { this.education = educationTrainers; }
    public int getReligious() { return religious; }
    public void setReligious(int religiousTrainers) { this.religious = religiousTrainers; }
    public int getOther() { return other; }
    public void setOther(int others) { this.other = others; }
    public District getDistrict() { return district; }
    public void setDistrict(District district) { this.district = district; }
    public int getCert() { return cert; }
    public void setCert(int certs) { this.cert = certs; }
    public int getRescue_scout() { return rescue_scout; }
    public void setRescue_scout(int rescue_scouts) { this.rescue_scout = rescue_scouts; }
}