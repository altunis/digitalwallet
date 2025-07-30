package digitalwallet.model;

import digitalwallet.dto.CustomerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String tckn;
    private String password;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getTckn() {
		return tckn;
	}
	public void setTckn(String tckn) {
		this.tckn = tckn;
	}
	public CustomerDto toDto() {
		return new CustomerDto(getName(), getSurname(),getTckn() , getPassword());
	}
    
    public static Customer fromDto(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setName(dto.name());
        customer.setSurname(dto.surname());
        customer.setTckn(dto.tckn());
        customer.setPassword(dto.password());
        return customer;
    }
}