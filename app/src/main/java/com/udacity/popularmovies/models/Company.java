package com.udacity.popularmovies.models;

/**
 * Created by jesse on 12/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class Company {
    private int companyId;
    private String companyLogoPath;
    private String companyName;
    private String companyCountry;

    public Company() {
    }

    public Company(int companyId, String companyLogoPath, String companyName, String companyCountry) {
        this.companyId = companyId;
        this.companyLogoPath = companyLogoPath;
        this.companyName = companyName;
        this.companyCountry = companyCountry;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyLogoPath() {
        return companyLogoPath;
    }

    public void setCompanyLogoPath(String companyLogoPath) {
        this.companyLogoPath = companyLogoPath;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCountry() {
        return companyCountry;
    }

    public void setCompanyCountry(String companyCountry) {
        this.companyCountry = companyCountry;
    }

    @Override
    public String toString() {
        return "\n\n" + "Company {" + "\n" +
                "companyId: " + companyId + ",\n" +
                "companyLogoPath: '" + companyLogoPath + '\'' + ",\n" +
                "companyName: '" + companyName + '\'' + ",\n" +
                "companyCountry: '" + companyCountry + '\'' + "\n" +
                '}' + ",\n";
    }
}
