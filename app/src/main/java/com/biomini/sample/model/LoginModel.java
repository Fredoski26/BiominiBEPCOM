package com.example.bepcom.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("status_code")
    @Expose
    private float status_code;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("personal_details")
    @Expose
    Personal_details personal_details;
    @SerializedName("Nok_informationObject")
    @Expose
    Nok_information Nok_informationObject;
    @SerializedName("Employment_informtionObject")
    @Expose
    Employment_informtion Employment_informtionObject;
    @SerializedName("Monthly_pensionObject")
    @Expose
    Monthly_pension Monthly_pensionObject;
    @SerializedName("GratuityObject")
    @Expose
     Gratuity GratuityObject;


    // Getter Methods

    public String getStatus() {
        return status;
    }

    public float getStatus_code() {
        return status_code;
    }

    public String getToken() {
        return token;
    }

    public Personal_details getPersonal_details() {
        return personal_details;
    }

    public Nok_information getNok_information() {
        return Nok_informationObject;
    }

    public Employment_informtion getEmployment_informtion() {
        return Employment_informtionObject;
    }

    public Monthly_pension getMonthly_pension() {
        return Monthly_pensionObject;
    }

    public Gratuity getGratuity() {
        return GratuityObject;
    }

    // Setter Methods

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus_code(float status_code) {
        this.status_code = status_code;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPersonal_details(Personal_details personal_details) {
        this.personal_details = personal_details;
    }

    public void setNok_information(Nok_information nok_informationObject) {
        this.Nok_informationObject = nok_informationObject;
    }

    public void setEmployment_informtion(Employment_informtion employment_informtionObject) {
        this.Employment_informtionObject = employment_informtionObject;
    }

    public void setMonthly_pension(Monthly_pension monthly_pensionObject) {
        this.Monthly_pensionObject = monthly_pensionObject;
    }

    public void setGratuity(Gratuity gratuityObject) {
        this.GratuityObject = gratuityObject;
    }

   public class Gratuity {
        @SerializedName("total_gratuity_amount")
        @Expose
    private float total_gratuity_amount;

       @SerializedName("death_pension_areas")
       @Expose
    private float death_pension_areas;

       @SerializedName("pension_areas")
       @Expose
    private float pension_areas;

       @SerializedName("pension_areas_150")
       @Expose
    private float pension_areas_150;

       @SerializedName("pension_areas_30")
       @Expose
    private float pension_areas_30;
       @SerializedName("federal_share")
       @Expose
    private float federal_share;
       @SerializedName("pension_areas_142")
       @Expose
    private float pension_areas_142;
       @SerializedName("other_increment_areas")
       @Expose
    private float other_increment_areas;
       @SerializedName("grand_total")
       @Expose
    private float grand_total;


    // Getter Methods

    public float getTotal_gratuity_amount() {
        return total_gratuity_amount;
    }

    public float getDeath_pension_areas() {
        return death_pension_areas;
    }

    public float getPension_areas() {
        return pension_areas;
    }

    public float getPension_areas_150() {
        return pension_areas_150;
    }

    public float getPension_areas_30() {
        return pension_areas_30;
    }

    public float getFederal_share() {
        return federal_share;
    }

    public float getPension_areas_142() {
        return pension_areas_142;
    }

    public float getOther_increment_areas() {
        return other_increment_areas;
    }

    public float getGrand_total() {
        return grand_total;
    }

    // Setter Methods

    public void setTotal_gratuity_amount(float total_gratuity_amount) {
        this.total_gratuity_amount = total_gratuity_amount;
    }

    public void setDeath_pension_areas(float death_pension_areas) {
        this.death_pension_areas = death_pension_areas;
    }

    public void setPension_areas(float pension_areas) {
        this.pension_areas = pension_areas;
    }

    public void setPension_areas_150(float pension_areas_150) {
        this.pension_areas_150 = pension_areas_150;
    }

    public void setPension_areas_30(float pension_areas_30) {
        this.pension_areas_30 = pension_areas_30;
    }

    public void setFederal_share(float federal_share) {
        this.federal_share = federal_share;
    }

    public void setPension_areas_142(float pension_areas_142) {
        this.pension_areas_142 = pension_areas_142;
    }

    public void setOther_increment_areas(float other_increment_areas) {
        this.other_increment_areas = other_increment_areas;
    }

    public void setGrand_total(float grand_total) {
        this.grand_total = grand_total;
    }
}
public class Monthly_pension {
    @SerializedName("initial_monthly_pension")
    @Expose
    private float initial_monthly_pension;
    @SerializedName("current_monthly_pension")
    @Expose
    private float current_monthly_pension;
    @SerializedName("pension_per_annum")
    @Expose
    private float pension_per_annum;
    @SerializedName("bank")
    @Expose
    private String bank;
    @SerializedName("account_number")
    @Expose
    private String account_number;
    @SerializedName("bvn")
    @Expose
    private String bvn;


    // Getter Methods

    public float getInitial_monthly_pension() {
        return initial_monthly_pension;
    }

    public float getCurrent_monthly_pension() {
        return current_monthly_pension;
    }

    public float getPension_per_annum() {
        return pension_per_annum;
    }

    public String getBank() {
        return bank;
    }

    public String getAccount_number() {
        return account_number;
    }

    public String getBvn() {
        return bvn;
    }

    // Setter Methods

    public void setInitial_monthly_pension(float initial_monthly_pension) {
        this.initial_monthly_pension = initial_monthly_pension;
    }

    public void setCurrent_monthly_pension(float current_monthly_pension) {
        this.current_monthly_pension = current_monthly_pension;
    }

    public void setPension_per_annum(float pension_per_annum) {
        this.pension_per_annum = pension_per_annum;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }
}
public class Employment_informtion {
    @SerializedName("appointment_date")
    @Expose
    private String appointment_date;
    @SerializedName("retirement_date")
    @Expose
    private String retirement_date;
    @SerializedName("duration_of_service")
    @Expose
    private float duration_of_service;
    @SerializedName("classification")
    @Expose
    private String classification;
    @SerializedName("rank")
    @Expose
    private String rank;
    @SerializedName("grade_level")
    @Expose
    private String grade_level;
    @SerializedName("step")
    @Expose
    private String step;
    @SerializedName("last_promotion_date")
    @Expose
    private String last_promotion_date;


    // Getter Methods

    public String getAppointment_date() {
        return appointment_date;
    }

    public String getRetirement_date() {
        return retirement_date;
    }

    public float getDuration_of_service() {
        return duration_of_service;
    }

    public String getClassification() {
        return classification;
    }

    public String getRank() {
        return rank;
    }

    public String getGrade_level() {
        return grade_level;
    }

    public String getStep() {
        return step;
    }

    public String getLast_promotion_date() {
        return last_promotion_date;
    }

    // Setter Methods

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public void setRetirement_date(String retirement_date) {
        this.retirement_date = retirement_date;
    }

    public void setDuration_of_service(float duration_of_service) {
        this.duration_of_service = duration_of_service;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setGrade_level(String grade_level) {
        this.grade_level = grade_level;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public void setLast_promotion_date(String last_promotion_date) {
        this.last_promotion_date = last_promotion_date;
    }
}
public class Nok_information {
    @SerializedName("nok_name")
    @Expose
    private String nok_name;
    @SerializedName("nok_phone")
    @Expose
    private String nok_phone;
    @SerializedName("nok_relationship")
    @Expose
    private String nok_relationship;
    @SerializedName("nok_address")
    @Expose
    private String nok_address;


    // Getter Methods

    public String getNok_name() {
        return nok_name;
    }

    public String getNok_phone() {
        return nok_phone;
    }

    public String getNok_relationship() {
        return nok_relationship;
    }

    public String getNok_address() {
        return nok_address;
    }

    // Setter Methods

    public void setNok_name(String nok_name) {
        this.nok_name = nok_name;
    }

    public void setNok_phone(String nok_phone) {
        this.nok_phone = nok_phone;
    }

    public void setNok_relationship(String nok_relationship) {
        this.nok_relationship = nok_relationship;
    }

    public void setNok_address(String nok_address) {
        this.nok_address = nok_address;
    }
}
public class Personal_details {
    @SerializedName("file_number")
    @Expose
    private String file_number;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("date_of_birth")
    @Expose
    private String date_of_birth;
    @SerializedName("phone_number")
    @Expose
    private String phone_number;
    @SerializedName("pension_category")
    @Expose
    private String pension_category;
    @SerializedName("payrol_status")
    @Expose
    private String payrol_status;
    @SerializedName("permanent_address")
    @Expose
    private String permanent_address;
    @SerializedName("local_government")
    @Expose
    private String local_government;
    @SerializedName("passport")
    @Expose
    private String passport;


    // Getter Methods

    public String getFile_number() {
        return file_number;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getPension_category() {
        return pension_category;
    }

    public String getPayrol_status() {
        return payrol_status;
    }

    public String getPermanent_address() {
        return permanent_address;
    }

    public String getLocal_government() {
        return local_government;
    }

    public String getPassport() {
        return passport;
    }

    // Setter Methods

    public void setFile_number(String file_number) {
        this.file_number = file_number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setPension_category(String pension_category) {
        this.pension_category = pension_category;
    }

    public void setPayrol_status(String payrol_status) {
        this.payrol_status = payrol_status;
    }

    public void setPermanent_address(String permanent_address) {
        this.permanent_address = permanent_address;
    }

    public void setLocal_government(String local_government) {
        this.local_government = local_government;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }
}
}
