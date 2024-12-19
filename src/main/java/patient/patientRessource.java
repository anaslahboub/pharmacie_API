package patient;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
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
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import metier.IPatientLocal;
import metier.entities.Commande;
import metier.entities.Ordonnance;
import metier.entities.Patient;
import metier.entities.Pharmacien;

@Path("/patient")
@RequestScoped
public class patientRessource {

    @EJB
    private IPatientLocal patientMetier;
    
    
   

    @POST
    @Path("/CreateAccount")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createPatient(PatientDTO patientDTO) {
        try {
        	
          
            // Vérifions si l'email existe déjà
            boolean emailExists = patientMetier.emailExists(patientDTO.getEmail());
            if (emailExists) {
                return ;
            }
            // On Crée un objet Patient à partir du DTO
            Patient patient = new Patient(patientDTO.getNom(),patientDTO.getPrenom(),patientDTO.getEmail(),patientDTO.getTelephone(),"patient",patientDTO.getPassword(),patientDTO.getLocalisation());

            // Enregistre le patient
            patientMetier.enregistrerPatient(patient);
            return ;
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
    }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(PatientDTO patientDTO) {
        // Vérifions si l'authentification est réussie
    	Patient patient = patientMetier.authenticatePatient(patientDTO.getEmail(), patientDTO.getPassword());
        if (patient != null) {
        	 Long patientId=patient.getId();
             return Response.ok(patientId).build();
        }
                     else {
            // Identifiants invalides
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("Email ou mot de passe incorrect.")
                           .build();
        }
    }
   

    @POST
    @Path("/NouvelleOrdonnance")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response NouvelleOrdonnance(@MultipartForm MultipartFormDataInput input) {

        try {
        	InputStream photoInputStream = input.getFormDataPart("ordonnanceImage", InputStream.class, null); 
        	String commentaire = input.getFormDataPart("commentaire", String.class, null);
        	Long patientId = input.getFormDataPart("patientId", Long.class, null); 
        	Long pharmacienId = input.getFormDataPart("pharmacienId", Long.class, null);
            // Lire l'image en tant que byte[]
            byte[] photoBytes = photoInputStream.readAllBytes();
         // Log des valeurs reçues 
            System.out.println("patientId: " + patientId); 
            System.out.println("pharmacienId: " + pharmacienId);
            System.out.println("COMMENTAIRE: " + commentaire);
            System.out.println("photoBytes: " + photoBytes);
            if (patientId == null || pharmacienId == null) { 
            	throw new IllegalArgumentException("Les IDs patient et pharmacien sont requis pour créer une ordonnance"); }
            // Créer une nouvelle ordonnance
            Ordonnance ordonnance = new Ordonnance();
            ordonnance.setPhoto(photoBytes);
            ordonnance.setCommentaire(commentaire);
            ordonnance.setDateEnvoie(new Date(System.currentTimeMillis()));
            ordonnance.setStatut("EN_ATTENTE"); // Statut initial de l'ordonnance
            
            // Sauvegarder l'ordonnance dans la base
            patientMetier.envoyerOrdonnance(patientId, ordonnance, pharmacienId);
        
            // Retourner une réponse de succès
            return Response.status(Response.Status.CREATED)
                    .entity("Ordonnance créée avec succès.")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors du traitement de l'image.")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Une erreur s'est produite lors de la création de l'ordonnance.")
                    .build();
        }
    }
    @GET
    @Path("/pharmacies")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPharmacies() {
        try {
            // Appelons la méthode métier pour récupérer toutes les pharmacies
            List<Pharmacien> pharmacies = patientMetier.getAllPharmacies();

            // Vérifier si des pharmacies sont disponibles
            if (pharmacies == null ) {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity("Aucune pharmacie trouvée.")
                        .build();
            }

            

            // Retourner les pharmacies avec un statut 200 (OK)
            return Response.ok(pharmacies).build();
        } catch (Exception e) {
            e.printStackTrace();
            // Gestion des erreurs internes
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des pharmacies !.")
                    .build();
        }
    }
    @GET
    @Path("/Ordonnances")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdonnances(@QueryParam("patientId") Long patientId  ) {
        try {
            // Appelons la méthode métier pour récupérer toutes les ordonnances
        	 List<Ordonnance> ordonnances = patientMetier.consulterOrdonnances(patientId);

            //On Vérifie si des ordonnances sont disponibles
            if (ordonnances == null ) {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity("Aucune Ordonnance trouvée.")
                        .build();
            }

          
            // Retourner les ordonnances avec un statut 200 (OK)
            return Response.ok(ordonnances).build();
        } catch (Exception e) {
            e.printStackTrace();
            // Gestion des erreurs internes
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des ordonnances !.")
                    .build();
        }
    }
    @GET
    @Path("/detailOrdonnance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response DetailOrdonnance(@QueryParam("ordonnanceId") Long ordonnanceId) {
    	try {
    		Ordonnance ordonnance = patientMetier.suivreOrdonnace(ordonnanceId);
    	if (ordonnance == null ) {
            return Response.status(Response.Status.NO_CONTENT)
                    .entity("Aucune Ordonnance trouvée.")
                    .build();
        }

        // Retourner les ordonnances avec un statut 200 (OK)
        return Response.ok(ordonnance).build();
    } catch (Exception e) {
        e.printStackTrace();
        // Gestion des erreurs internes
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Erreur lors de la récupération de l'ordonnance !.")
                .build();
    }
    }
    @GET
    @Path("/profil")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPatientProfil(@QueryParam("patientId") Long patientId) {
        try {
            if (patientId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("L'ID du patient est requis.")
                        .build();
            }

            Patient patient = patientMetier.findPatient(patientId);
            if (patient == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Aucun patient trouvé.")
                        .build();
            }

          
            return Response.ok(patient).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur interne du serveur.")
                    .build();
        }
    }
    @PUT
    @Path("/profilUpdate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void updatePatientProfil(PatientDTO patientDTO,
    		@QueryParam("patientId") Long patientId) {
        try {
            if (patientDTO == null || patientDTO.getEmail() == null|| patientDTO.getPassword() == null) {
                return ;
            }

            Patient patient = patientMetier.findPatient(patientId);
            if (patient == null) {
                return ;
            }

            // Mise à jour des données
            patient.setNom(patientDTO.getNom());
            patient.setPrenom(patientDTO.getPrenom());
            patient.setTelephone(patientDTO.getTelephone());
            patient.setEmail(patientDTO.getEmail());
            patient.setPassword(patientDTO.getPassword());

            patientMetier.modifierPatient(patient);

            return ;
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
    }
    @GET
    @Path("/dashboard")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardStats(@QueryParam("patientId") Long patientId) {
        try {
            // Calcul des données dynamiques pour le tableau de bord
            Map<String, Integer> stats = new HashMap<>();
            stats.put("ordonnancesSoumises", patientMetier.getOrdonnancesSoumisesCount(patientId));
            stats.put("commandesEnCours", patientMetier.getCommandesEnCoursCount(patientId));
            stats.put("commandesCompletees", patientMetier.getCommandesCompleteesCount(patientId));

            return Response.ok(stats).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des statistiques.")
                    .build();
        }
    }
    @GET
    @Path("/header")
    @Produces(MediaType.APPLICATION_JSON)
    public Response patientName(@QueryParam("patientId") Long patientId) {
        try {
        	String Prenom =patientMetier.findPatient(patientId).getPrenom();
            return Response.ok(Prenom).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la récupération des statistiques.")
                    .build();
        }
    }
    
    
    @GET
    @Path("/commandes/{id_patient}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consulterHistoriqueCommandes(@PathParam("id_patient") Long id_patient) {
    	List<Commande> commandes = patientMetier.consulterHistoriqueCommandes(id_patient);
    	if (commandes ==null) {
    		return Response.ok("erreur d'extraction des commandes ").build();
    	}
    	return Response.ok(commandes).build();
    	
    }
    
    @GET
    @Path("/commande/{id_commande}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consulterUneCommande(@PathParam("id_commande") Long id_commande) {
    	Commande commande = patientMetier.consulterUneCommande(id_commande);
    	if (commande ==null) {
    		return Response.status(Response.Status.NOT_FOUND).entity("erreur d'extraction de la commande ").build();
    	}
    	return Response.ok(commande).build();
    	
    }
    
}
