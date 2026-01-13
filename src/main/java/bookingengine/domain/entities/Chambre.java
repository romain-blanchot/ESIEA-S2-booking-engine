package bookingengine.domain.entities;

public class Chambre {
    private Long id;
    private String numero;
    private String type;
    private double prixBase;
    private int capacite;
    private String description;
    private boolean disponible;

    public Chambre() {}

    public Chambre(Long id, String numero, String type, double prixBase, int capacite, String description, boolean disponible) {
        this.id = id;
        this.numero = numero;
        this.type = type;
        this.prixBase = prixBase;
        this.capacite = capacite;
        this.description = description;
        this.disponible = disponible;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrixBase() { return prixBase; }
    public void setPrixBase(double prixBase) { this.prixBase = prixBase; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
