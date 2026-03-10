package com.ayd2.congress.services;



import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import com.ayd2.congress.repositories.UserRepository;
import com.ayd2.congress.services.jwt.JwtService;
import com.ayd2.congress.services.jwt.JwtServiceImpl;



public class JwtServiceTest {

 
  private static final String SECRET_KEY = "MI-SECRET-KEY-EXAMPLE-TEST?12354678910PRUEBAPUEASDF";
  private static final long EXPSECONDS = 1;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private JwtService service;

  @BeforeEach
  void setUp() {
    service = new JwtServiceImpl(SECRET_KEY, EXPSECONDS, userRepository);
  }
}
