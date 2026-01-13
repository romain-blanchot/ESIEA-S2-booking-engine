package bookingengine;

import org.springframework.boot.SpringApplication;

public class TestBookingEngineApplication {

	public static void main(String[] args) {
		SpringApplication.from(BookingEngineApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
