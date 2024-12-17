package pharmacie;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import metier.IPharmacienLocal;
import metier.PharmacienImpl;
import metier.entities.Commande;

import metier.entities.Ordonnance;
import metier.entities.Pharmacien;

import java.util.List;


@Path("/pharmacien")
public class PharmacienRessource {
    
    @EJB
    private IPharmacienLocal pharmacienService;
    public PharmacienRessource() {
		super();
		pharmacienService = new PharmacienImpl();
	}
    
    @GET
    @Path("/ordonnances/{idPharmacien}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getOrdonnancesByPharmacien(@PathParam("idPharmacien") Long idPharmacien) {
            List<Ordonnance> ordonnances = pharmacienService.consulterOrdonnancesReçues(idPharmacien);
            return Response.ok(ordonnances).build();
    }
    
    @GET
    @Path("/commandes/{idPharmacien}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommandes(@PathParam("idPharmacien") Long idPharmacien) {
        try {
            List<Commande> commandes = pharmacienService.consulterCommandes(idPharmacien);
            return Response.ok(commandes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des commandes : " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/ordonnance/{idOrdonnance}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifierOrdonnance(@PathParam("idOrdonnance") Long idOrdonnance) {
        try {
            Ordonnance ordonnance = pharmacienService.verifierOrdonnance(idOrdonnance);
            if (ordonnance == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Ordonnance non trouvée")
                        .build();
            }
            return Response.ok(ordonnance).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la vérification de l'ordonnance : " + e.getMessage())
                    .build();
        }
    } 
    
    
    @GET
    @Path("/commande/{idCommande}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifierCommande (@PathParam("idCommande") Long idCommande) {
    	Commande commande = pharmacienService.verifierCommande(idCommande);
    	 if (commande == null) {
             return Response.status(Response.Status.NOT_FOUND)
                     .entity("Ordonnance non trouvée")
                     .build();
         }
         return Response.ok(commande).build();
    }
    
    
    @POST
    @Path("/ordonnance/{idOrdonnance}/accepter")
    @Produces(MediaType.APPLICATION_JSON)		
    public Response accepterOrdonnance(
            @PathParam("idOrdonnance") Long idOrdonnance, @QueryParam("commStatus") String commStatus, @QueryParam("montantTotal") Double montantTotal) {
        	Commande commande =pharmacienService.accepterOrdonnance(idOrdonnance, commStatus,  montantTotal);
        	return Response.ok(commande).build();
    }
  
    
 
    @PUT
    @Path("/ordonnance/{idOrdonnance}/rejeter")
    @Produces(MediaType.APPLICATION_JSON)
    public void rejeterOrdonnance( @PathParam("idOrdonnance") Long idOrdonnance) {
            pharmacienService.rejeterOrdonnance(idOrdonnance);
            return ;
    }
    
    @PUT
    @Path("/commande/{idCommande}/changer")
    @Produces(MediaType.APPLICATION_JSON)
    public void changerStatusCommande(@PathParam("idCommande") Long idCommande ,@QueryParam("commStatus") String commStatus) {
    	 pharmacienService.changerStatusCommande(idCommande, commStatus);
    	return ;
    }
      
    @POST
    @Path("/commande/{idCommande}/notifier")
    public Response notifierPatient(
            @PathParam("idCommande") Long idCommande) {
        try {
            pharmacienService.notifierParEmail(idCommande);
            return Response.ok("Patient notifié avec succès").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la notification : " + e.getMessage())
                    .build();
        }
    } 
     
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Long login(LoginRequest loginRequest) {
        Pharmacien pharmacien = pharmacienService.loginPharmacie(loginRequest.email, loginRequest.password);
        
        pharmacien.setIsActive("active");
      
            return pharmacien.getId();
    }

    public static class LoginRequest {
        public String email;
        public String password;
        public LoginRequest() {
		}
		public LoginRequest(String email, String password) {
			super();
			this.email = email;
			this.password = password;
		}
        

		
    }
    
    
    @POST
    @Path("/signIn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(SignInRequest signInRequest) {
    	pharmacienService.creerpharmacie(
    			signInRequest.getNom(),
    	        signInRequest.getPrenom(),
    	        signInRequest.getEmail(),
    	        signInRequest.getTelephone(),
    	        signInRequest.getPassword(),
    	        signInRequest.getLongitude(),
    	        signInRequest.getLatitude(),
    	        signInRequest.getNomMap());
    	return Response.ok("le pharmacie a bien créer ").build();
       
    }
    public static class SignInRequest {
        private String nom;
        private String prenom;
        private String email;
        private String telephone;
        private String password;
    	private Double longitude;
        private Double latitude;
        private String nomMap;
      
        public SignInRequest() {
			super();
		}

		public  SignInRequest(String nom, String prenom, String email, String telephone, String password,
				Double longitude, Double latitude, String nomMap) {
			super();
			this.nom = nom;
			this.prenom = prenom;
			this.email = email;
			this.telephone = telephone;
			this.password = password;
			this.longitude = longitude;
			this.latitude = latitude;
			this.nomMap = nomMap;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	

        
        public SignInRequest(String nom, String prenom, String email, String telephone, Double longitude, Double latitude, String nomMap) {
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.telephone = telephone;
            this.longitude = longitude;
            this.latitude = latitude;
            this.nomMap = nomMap;
        }

        // Getters et setters
        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getNomMap() {
            return nomMap;
        }

        public void setNomMap(String nomMap) {
            this.nomMap = nomMap;
        }
    }
    
    
    @PUT
    @Path("/{id}/modifierPharmacie")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierPharmacie(@PathParam("id") Long idPharmacien, SignInRequest signInRequest) {
        try {
            pharmacienService.modifierPharmacie(
                    idPharmacien,
                    signInRequest.getNom(),
                    signInRequest.getPrenom(),
                    signInRequest.getEmail(),
                    signInRequest.getTelephone(),
                    signInRequest.getLongitude(),
                    signInRequest.getLatitude(),
                    signInRequest.getNomMap()
            );
            return Response.ok("Les informations de la pharmacie ont bien été modifiées.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erreur lors de la mise à jour des informations de la pharmacie.").build();
        }
    }
    
    
    @GET 
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public 	Response trouverPharmacieparId(@PathParam("id") Long id) {
    	Pharmacien pharmacie = pharmacienService.trouverPharmacieparId(id);
    	if(pharmacie == null) {
    		return Response.ok("le pharmacie nest pa strouver oi id incorrect").build();
    	}
    	return Response.ok(pharmacie).build();
    }
    
    
    @GET
    @Path("/ordonnances/{idPharmacien}/acceptees")
    public Response ordonnancesAcceptees(@PathParam("idPharmacien") Long idPharmacien) {
    	int count = pharmacienService.calculerNombreOrdonnancesAcceptees(idPharmacien);
    	return Response.ok(count).build();
    }
    
    

    @GET
    @Path("/ordonnances/{idPharmacien}/encours")
    public Response ordonnancesEncours(@PathParam("idPharmacien") Long idPharmacien) {
    	int count = pharmacienService.calculerNombreOrdonnancesEnCours(idPharmacien);
    	return Response.ok(count).build();
    }
    
    
    @GET
    @Path("/ordonnances/{idPharmacien}/totale")
    public Response ordonnancesTotale(@PathParam("idPharmacien") Long idPharmacien) {
    	int count = pharmacienService.calculerNombreTotalOrdonnances(idPharmacien);
    	return Response.ok(count).build();
    }
    

    
    
    
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        return Response.ok("Service Pharmacien opérationnel").build();
    }
}