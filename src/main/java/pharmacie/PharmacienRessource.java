package pharmacie;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

    /**
     * Vérification d'une ordonnance
     * @param idOrdonnance Identifiant de l'ordonnance
     * @return Ordonnance si trouvée, erreur sinon*/
    
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

    /**
     * Acceptation d'une ordonnance
     * @param idOrdonnance Identifiant de l'ordonnance
     * @param commStatus Statut de la commande
     * @param ordoStatus Statut de l'ordonnance
     * @param montantTotal Montant total de la commande
     * @return Message de confirmation
   
    @POST
    @Path("/ordonnance/{idOrdonnance}/accepter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response accepterOrdonnance(
            @PathParam("idOrdonnance") Long idOrdonnance,
            @QueryParam("commStatus") String commStatus,
            @QueryParam("ordoStatus") String ordoStatus,
            @QueryParam("montantTotal") Double montantTotal) {
        try {
            pharmacienService.accepterOrdonnance(idOrdonnance, commStatus, ordoStatus, montantTotal);
            return Response.ok("Ordonnance acceptée avec succès").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de l'acceptation de l'ordonnance : " + e.getMessage())
                    .build();
        }
    }  */

    /**
     * Rejet d'une ordonnance
     * @param idOrdonnance Identifiant de l'ordonnance
     * @param status Statut de l'ordonnance
     * @return Message de confirmation
     
    @POST
    @Path("/ordonnance/{idOrdonnance}/rejeter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rejeterOrdonnance(
            @PathParam("idOrdonnance") Long idOrdonnance,
            @QueryParam("status") String status) {
        try {
            pharmacienService.rejeterOrdonnance(idOrdonnance, status);
            return Response.ok("Ordonnance rejetée avec succès").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors du rejet de l'ordonnance : " + e.getMessage())
                    .build();
        }
    }*/

    /**
     * Notification du patient sur l'état de sa commande
     * @param idCommande Identifiant de la commande
     * @param status Statut de la commande
     * @param montantTotal Montant total de la commande
     * @return Message de confirmation
    
    @POST
    @Path("/commande/{idCommande}/notifier")
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifierPatient(
            @PathParam("idCommande") Long idCommande,
            @QueryParam("status") String status,
            @QueryParam("montantTotal") Double montantTotal) {
        try {
            pharmacienService.notifierPatientEtatCommande(idCommande, status, montantTotal);
            return Response.ok("Patient notifié avec succès").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erreur lors de la notification : " + e.getMessage())
                    .build();
        }
    } */

    /**
     * Récupération des commandes pour un pharmacien
     * @param idPharmacien Identifiant du pharmacien
     * @return Liste des commandes*/
     
    

    /**
     * Endpoint de test de connexion
     * @return Un message de confirmation
     */
    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public Response ping() {
        return Response.ok("Service Pharmacien opérationnel").build();
    }
}