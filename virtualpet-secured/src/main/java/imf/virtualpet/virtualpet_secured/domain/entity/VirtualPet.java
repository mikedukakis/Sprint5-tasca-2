package imf.virtualpet.virtualpet_secured.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
public class VirtualPet {
    @Id
    private String id;
    @Field("Name")
    private String name;
    @Field("PetType")
    private PetType petType;
    @Field("Colour")
    private String colour;


}
