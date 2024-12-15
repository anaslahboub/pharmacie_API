package patient;

import java.sql.Date;

public class OrdonnanceDTO {
    private Long id;
    private byte[] photo;
    private String statut;
    private String commentaire;
    private Date date;
    private String pharmacienNom;

    // Constructeur
    public OrdonnanceDTO(Long id, byte[] photo, String statut, String commentaire, Date date, String pharmacienNom) {
        this.id = id;
        this.photo = photo;
        this.statut = statut;
        this.commentaire = commentaire;
        this.date = date;
        this.pharmacienNom = pharmacienNom;
    }

    public OrdonnanceDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrdonnanceDTO(Long id, String statut, Date date) {
		super();
		this.id = id;
		this.statut = statut;
		this.date = date;
	}

	// Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPharmacienNom() {
        return pharmacienNom;
    }

    public void setPharmacienNom(String pharmacienNom) {
        this.pharmacienNom = pharmacienNom;
    }
}
