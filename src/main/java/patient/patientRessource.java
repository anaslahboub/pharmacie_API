package patient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import metier.IPatientLocal;
import metier.IPharmacienLocal;
import metier.entities.Ordonnance;
import metier.entities.Patient;
import metier.entities.Pharmacien;
import metier.entities.Utilisateur;

@Path("/patient")
@RequestScoped
public class patientRessource {

    @EJB
    private IPatientLocal patientMetier;

    @POST
    @Path("/CreateAccount")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPatient(PatientDTO patientDTO) {
        try {
        	
          
            // Vérifier si l'email existe déjà
            boolean emailExists = patientMetier.emailExists(patientDTO.getEmail());
            if (emailExists) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Un compte avec cet email existe déjà.")
                        .build();
            }

            // Créer un objet Patient à partir du DTO
            Patient patient = new Patient(patientDTO.getNom(),patientDTO.getPrenom(),patientDTO.getEmail(),patientDTO.getTelephone(),patientDTO.getPassword(),"patient",patientDTO.getLocalisation());
           

            // Enregistrer le patient
            patientMetier.enregistrerPatient(patient);

            return Response.status(Response.Status.CREATED)
                    .entity("Compte créé avec succès.")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la création du compte patient.")
                    .build();
        }
    }
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(PatientDTO patientDTO) {
        // Vérifiez si l'authentification est réussie
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
            ordonnance.setDate(new Date(System.currentTimeMillis()));
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
            // Appeler la méthode métier pour récupérer toutes les pharmacies
            List<Pharmacien> pharmacies = patientMetier.getAllPharmacies();

            // Vérifier si des pharmacies sont disponibles
            if (pharmacies == null ) {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity("Aucune pharmacie trouvée.")
                        .build();
            }

            List<PharmacienDTO> pharmaciesDTO = pharmacies.stream()
                    .map(pharmacien -> new PharmacienDTO(
                            pharmacien.getId(),                        // id
                            pharmacien.getLocalisation().getNomMap(),  // nomMap
                            pharmacien.getLocalisation().getLongitude(), // longitude
                            pharmacien.getLocalisation().getLatitude(),  // latitude
                            pharmacien.isActive()
                    ))
                    .collect(Collectors.toList());

            // Retourner les pharmacies avec un statut 200 (OK)
            return Response.ok(pharmaciesDTO).build();
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
            // Appeler la méthode métier pour récupérer toutes les ordonnances
        	 List<Ordonnance> ordonnances = patientMetier.consulterOrdonnances(patientId);

            // Vérifier si des ordonnances sont disponibles
            if (ordonnances == null ) {
                return Response.status(Response.Status.NO_CONTENT)
                        .entity("Aucune Ordonnance trouvée.")
                        .build();
            }

            List<OrdonnanceDTO> ordonnancesDTO = ordonnances.stream()
                    .map(ordonnance -> new OrdonnanceDTO(
                            ordonnance.getId(),                         // id
                            ordonnance.getStatut(),                     // statut
                            ordonnance.getDate()                     // date
                    ))
                    .collect(Collectors.toList());

            // Retourner les ordonnances avec un statut 200 (OK)
            return Response.ok(ordonnancesDTO).build();
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

    	OrdonnanceDTO ordonnancesDTO = 
          new OrdonnanceDTO(
                        ordonnance.getId(),                         // id
                        ordonnance.getPhoto(),                      // photo
                        ordonnance.getStatut(),                     // statut
                        ordonnance.getCommentaire(),                // commentaire
                        ordonnance.getDate(),                       // date
                        ordonnance.getPharmacien().getNom()         // pharmacienNom
                );

        // Retourner les ordonnances avec un statut 200 (OK)
        return Response.ok(ordonnancesDTO).build();
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

            Map<String, Object> response = new HashMap<>();
            response.put("nom", patient.getNom());
            response.put("prenom", patient.getPrenom());
            response.put("telephone", patient.getTelephone());
            response.put("email", patient.getEmail());
            response.put("password", patient.getPassword());

            return Response.ok(response).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur interne du serveur.")
                    .build();
        }
    }

    @POST
    @Path("/profilUpdate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePatientProfil(PatientDTO patientDTO,
    		@QueryParam("patientId") Long patientId) {
        try {
            if (patientDTO == null || patientDTO.getEmail() == null|| patientDTO.getPassword() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Les données du profil sont invalides.")
                        .build();
            }

            Patient patient = patientMetier.findPatient(patientId);
            if (patient == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Aucun patient correspondant trouvé.")
                        .build();
            }

            // Mise à jour des données
            patient.setNom(patientDTO.getNom());
            patient.setPrenom(patientDTO.getPrenom());
            patient.setTelephone(patientDTO.getTelephone());
            patient.setEmail(patientDTO.getEmail());
            patient.setPassword(patientDTO.getPassword());

            patientMetier.modifierPatient(patient);

            return Response.ok("Profil modifié avec succès.").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la mise à jour du profil.")
                    .build();
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

    
}
