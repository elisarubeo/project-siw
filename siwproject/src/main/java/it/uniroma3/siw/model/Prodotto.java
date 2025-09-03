package it.uniroma3.siw.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class Prodotto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotNull(message = "Il prezzo è obbligatorio")
    @Positive(message = "Il prezzo deve essere maggiore di 0")
    private Double prezzo;

    // opzionale
    private String descrizione;

    @ManyToMany
    private Set<Categoria> categorie;

    /**
     * Relazione auto-referenziale per i "prodotti simili".
     * Lato owning: scrive sulla join table.
     */
    @ManyToMany
    @JoinTable(
        name = "prodotto_simili",
        joinColumns = @JoinColumn(name = "prodotto_id"),
        inverseJoinColumns = @JoinColumn(name = "simile_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"prodotto_id", "simile_id"})
    )
    private Set<Prodotto> simili = new HashSet<>();

    /**
     * Lato inverso della relazione, non scrive sulla join table.
     */
    @ManyToMany(mappedBy = "simili")
    private Set<Prodotto> simileDi = new HashSet<>();

    /* ======================= Helper “simili” ======================= */

    /** Unione di simili e simileDi (simmetrico). */
    @Transient
    public Set<Prodotto> getTuttiISimili() {
        Set<Prodotto> all = new HashSet<>(simili);
        all.addAll(simileDi);
        return all;
    }

    /** Aggiunge b tra i simili, impedendo self-link e mantenendo la simmetria. */
    public void addSimile(Prodotto b) {
        if (b == null) return;
        if (this.equals(b)) {
            throw new IllegalArgumentException("Un prodotto non può essere simile a se stesso.");
        }
        if (this.simili.add(b)) {
            b.simileDi.add(this);
        }
    }

    /** Rimuove b dai simili, mantenendo la simmetria. */
    public void removeSimile(Prodotto b) {
        if (b == null) return;
        if (this.simili.remove(b)) {
            b.simileDi.remove(this);
        }
    }

    /* ======================= Getters/Setters ======================= */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Set<Categoria> getCategorie() {
        return categorie;
    }

    public void setCategorie(Set<Categoria> categorie) {
        this.categorie = categorie;
    }

    public Set<Prodotto> getSimili() {
        return simili;
    }

    public void setSimili(Set<Prodotto> simili) {
        this.simili = simili;
    }

    public Set<Prodotto> getSimileDi() {
        return simileDi;
    }

    public void setSimileDi(Set<Prodotto> simileDi) {
        this.simileDi = simileDi;
    }

    /* ======================= equals/hashCode ======================= */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Prodotto other)) return false;
        return id != null && id.equals(other.id);
    }
}
