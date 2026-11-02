package com.courier.logistics.entity;
import com.fasterxml.jackson.annotation.JsonIgnore; import jakarta.persistence.*; import jakarta.validation.constraints.*; import java.time.LocalDateTime;
@Entity @Table(name="payments")
public class Payment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @PositiveOrZero @Column(nullable=false) private Double amount;
 @Enumerated(EnumType.STRING) private PaymentMethod paymentMethod=PaymentMethod.COD;
 @Enumerated(EnumType.STRING) private PaymentStatus paymentStatus=PaymentStatus.PENDING;
 private LocalDateTime paymentDateTime;
 @JsonIgnore @OneToOne(mappedBy="payment") private Shipment shipment;
 public Payment(){} public Long getId(){return id;} public void setId(Long id){this.id=id;} public Double getAmount(){return amount;} public void setAmount(Double v){amount=v;} public PaymentMethod getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(PaymentMethod v){paymentMethod=v;} public PaymentStatus getPaymentStatus(){return paymentStatus;} public void setPaymentStatus(PaymentStatus v){paymentStatus=v;} public LocalDateTime getPaymentDateTime(){return paymentDateTime;} public void setPaymentDateTime(LocalDateTime v){paymentDateTime=v;} public Shipment getShipment(){return shipment;} public void setShipment(Shipment v){shipment=v;}
}
