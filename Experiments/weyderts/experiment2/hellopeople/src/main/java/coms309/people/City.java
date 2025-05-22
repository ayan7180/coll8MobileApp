package coms309.people;

public class City {

    ///////////////////////////////////
    //  Private Variables
    ///////////////////////////////////
    /**
     * name (String): The name of the city
     * country (String): The country the city resides in
     * areaCode (String): The telephone area code of the city
     * population (String): The population of the city
     */
    private String name;
    private String country;
    private String areaCode;
    private String population;

    ///////////////////////////////////
    //  Constructors
    ///////////////////////////////////
    // FIXME A default constructor makes it so POST requests never call the correct constructor, so all args are NULL'd out
//    public City(){
//
//    }

    public City(String name, String country, String areaCode, String population){
        this.name = name;
        this.country = country;
        this.areaCode = areaCode;
        this.population = population;
    }

    ///////////////////////////////////
    //  Getters
    ///////////////////////////////////
    public String get_name(){ return this.name; }
    public String get_country(){ return this.country; }
    public String get_areaCode(){ return this.areaCode; }
    public String get_population(){ return this.population; }

    ///////////////////////////////////
    //  Setters
    ///////////////////////////////////
    public void set_name(String name){ this.name = name; }
    public void set_country(String country){ this.country = country; }
    public void set_areaCode(String areaCode){ this.areaCode = areaCode; }
    public void set_population(String population){ this.population = population; }

    @Override
    public String toString() {
        return name + " "
                + country + " "
                + areaCode + " "
                + population;
    }

}
