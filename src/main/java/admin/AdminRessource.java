package admin;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import metier.AdminEJBImpl;
import metier.IAdminLocal;
import metier.IPharmacienLocal;
import metier.PharmacienImpl;
import metier.entities.Commande;
import metier.entities.Ordonnance;
import metier.entities.Patient;
import metier.entities.Pharmacien;

import java.util.List;
@Path("/admin")
public class AdminRessource {
	@EJB
	private IAdminLocal adminservice ;
	
	public AdminRessource() {
		super();
		adminservice= new AdminEJBImpl();
		// TODO Auto-generated constructor stub
	}

	@GET
	@Path("/patients")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getallpatient() {
		List<Patient> patients = adminservice.consulterInformationsPatient();
		return Response.ok(patients).build();
	}
	
	@GET
	@Path("/pharmacies")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getallpharmacies() {
		List<Pharmacien> pharmaciens = adminservice.consulterInformationsPharmacien();
		return Response.ok(pharmaciens).build();
	}
	@GET
	@Path("/patient/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getpatientby_id(@PathParam( "id" ) Long id ) {
		Patient patient = adminservice.consulterPatientParId(id);
		return Response.ok(patient).build();
	}
	
	@GET
	@Path("/pharmacie/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getpharmacieby_id(@PathParam( "id" ) Long id ) {
		Pharmacien Pharmacien = adminservice.consulterPharmacieParId(id);
		return Response.ok(Pharmacien).build();
	}
	@PUT
	@Path("/pharmacie/{id}/changer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changerstatus_pharmacien(@PathParam( "id" ) Long id ) {
		Pharmacien Pharmacien = adminservice.changerPartnerStatusPharmacien(id);
		
	}
	@GET
	@Path("/pharmaciees/partners") // Ajustement pour éviter toute ambiguïté
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPartnerPharmacies() {
	    List<Pharmacien> pharmacienList = adminservice.ispartner();
	    return Response.ok(pharmacienList).build();
	}

	@GET
	@Path("/nbrpatients")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getnbrpatients() {
		int nbrpatients = adminservice.calculnbrpatient();
		return Response.ok(nbrpatients).build();
	}

	@GET
	@Path("/nbrpharmacies")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getnbrPharmacies() {
		int nbrpharmacies = adminservice.calculnbrpharmacie();
		return Response.ok(nbrpharmacies).build();
	}
	@GET
	@Path("/nbrpharmacie/partenaire")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getnbrPharmaciespartenaire() {
		int nbrpharmaciesp = adminservice.calculpharmaciepartenaire();
		return Response.ok(nbrpharmaciesp).build();
	}
	@GET
	@Path("/nbrpharmacie/nonpartenaire")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getnbrPharmaciesnonpartenaire() {
		int nbrpharmaciesnp = adminservice.calculpharmacienonpartenaire();
		return Response.ok(nbrpharmaciesnp).build();
	}
	
	@DELETE
    @Path("/deleteuser/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteuser(@PathParam( "id" ) Long id ) {
         adminservice.deleteUser(id);
    }

}
