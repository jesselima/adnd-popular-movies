package dev.jesselima.jmovies.models;

/**
 * Created by jesse on 12/08/18.
 * This is a part of the project adnd-popular-movies.
 */
public class MovieProductionCompany {
    private int companyId;
    private String companyLogoPath;
    private String companyName;
    private String companyCountry;

    public MovieProductionCompany() {
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

    public void setCompanyCountry(String companyCountry) {
        this.companyCountry = companyCountry;
    }

    @Override
    public String toString() {
        return "\n" + "MovieProductionCompany {" + "\n" +
                "companyId: "       + companyId + ",\n" +
                "companyLogoPath: '"+ companyLogoPath + '\'' + ",\n" +
                "companyName: '"    + companyName + '\'' + ",\n" +
                "companyCountry: '" + companyCountry + '\'' + "\n" +
                '}';
    }
}
