package patient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PharmacienDTO {
	@JsonProperty("id")
    private Long id;
	@JsonProperty("nomMap")
    private String nomMap;
	@JsonProperty("longitude")
    private Double longitude;
	@JsonProperty("latitude")
    private Double latitude;
	@JsonProperty("isActive")
	private String isActive;

    // Constructeur et getters/setters
    public PharmacienDTO(Long id, String nomMap, Double longitude, Double latitude,String isActive) {
        this.id = id;
        this.nomMap = nomMap;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isActive= isActive;
    }

	public PharmacienDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}

