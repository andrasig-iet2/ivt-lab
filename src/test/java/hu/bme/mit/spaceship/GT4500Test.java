package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

public class GT4500Test {

  private GT4500 ship;
  private TorpedoStore mockPrimaryTS;
  private TorpedoStore mockSecondaryTS;

  @BeforeEach
  public void init(){
    mockPrimaryTS = mock(TorpedoStore.class);
    mockSecondaryTS = mock(TorpedoStore.class);
    this.ship = new GT4500(mockPrimaryTS, mockSecondaryTS);
  }

  @Test
  public void fireTorpedo_Single_Success(){
    // Arrange
    when(mockPrimaryTS.isEmpty()).thenReturn(true);
    when(mockPrimaryTS.fire(1)).thenReturn(false);
    when(mockSecondaryTS.isEmpty()).thenReturn(false);
    when(mockSecondaryTS.fire(1)).thenReturn(true);
    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);
    
    // Assert
    assertEquals(true, result);
    verify(mockPrimaryTS, times(1)).isEmpty();
    verify(mockSecondaryTS, times(1)).isEmpty();
    verify(mockSecondaryTS, times(1)).fire(1);
  }
  
  @Test
  public void fireTorpedo_All_Success(){
    // Arrange
    when(mockPrimaryTS.isEmpty()).thenReturn(false);
    when(mockPrimaryTS.fire(1)).thenReturn(true);
    when(mockSecondaryTS.isEmpty()).thenReturn(false);
    when(mockSecondaryTS.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    assertEquals(true, result);
    verify(mockPrimaryTS, times(1)).isEmpty();
    verify(mockPrimaryTS, times(1)).fire(1);
    verify(mockSecondaryTS, times(1)).isEmpty();
    verify(mockSecondaryTS, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Tries_PrimaryTS_First() {
    when(mockPrimaryTS.isEmpty()).thenReturn(false);
    when(mockPrimaryTS.fire(1)).thenReturn(true);
    when(mockSecondaryTS.isEmpty()).thenReturn(false);
    when(mockSecondaryTS.fire(1)).thenReturn(true);

    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    assertEquals(true, result);
    verify(mockPrimaryTS, times(1)).isEmpty();
    verify(mockPrimaryTS, times(1)).fire(1);
    verify(mockSecondaryTS, never()).isEmpty();
    verify(mockSecondaryTS, never()).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Tries_AlternatingTS() {
    when(mockPrimaryTS.isEmpty()).thenReturn(false);
    when(mockPrimaryTS.fire(1)).thenReturn(true);
    when(mockSecondaryTS.isEmpty()).thenReturn(false);
    when(mockSecondaryTS.fire(1)).thenReturn(true);

    boolean result1 = ship.fireTorpedo(FiringMode.SINGLE);
    boolean result2 = ship.fireTorpedo(FiringMode.SINGLE);
    boolean result3 = ship.fireTorpedo(FiringMode.SINGLE);
    
    assertEquals(true, result1);
    assertEquals(true, result2);
    assertEquals(true, result3);
    
    InOrder inOrder = inOrder(mockPrimaryTS, mockSecondaryTS);
    
    inOrder.verify(mockPrimaryTS).isEmpty();
    inOrder.verify(mockPrimaryTS).fire(1);
    inOrder.verify(mockSecondaryTS).isEmpty();
    inOrder.verify(mockSecondaryTS).fire(1);
    inOrder.verify(mockPrimaryTS).isEmpty();
    inOrder.verify(mockPrimaryTS).fire(1);
    
  }
  
  @Test
  public void fireTorpedo_Single_Tries_BothTS_If_NextTS_isEmpty() {
    when(mockPrimaryTS.isEmpty()).thenReturn(true);
    when(mockPrimaryTS.fire(1)).thenReturn(false);
    when(mockSecondaryTS.isEmpty()).thenReturn(false);
    when(mockSecondaryTS.fire(1)).thenReturn(true);
    
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);
    
    assertEquals(true, result);

    InOrder inOrder = inOrder(mockPrimaryTS, mockSecondaryTS);
    
    inOrder.verify(mockPrimaryTS).isEmpty();
    inOrder.verify(mockSecondaryTS).isEmpty();
    inOrder.verify(mockSecondaryTS).fire(1);

    verify(mockPrimaryTS, times(1)).isEmpty();
    verify(mockPrimaryTS, never()).fire(1);
    verify(mockSecondaryTS, times(1)).isEmpty();
    verify(mockSecondaryTS, times(1)).fire(1);
  }
  
  @Test
  public void fireTorpedo_Single_Fails_If_NextTS_Fails() {
    when(mockPrimaryTS.isEmpty()).thenReturn(false);
    when(mockPrimaryTS.fire(1)).thenReturn(false);
    when(mockSecondaryTS.isEmpty()).thenReturn(false);
    when(mockSecondaryTS.fire(1)).thenReturn(true);

    boolean result = ship.fireTorpedo(FiringMode.SINGLE);
    
    assertEquals(false, result);

    verify(mockPrimaryTS, times(1)).isEmpty();
    verify(mockPrimaryTS, times(1)).fire(1);
    verify(mockSecondaryTS, never()).isEmpty();
    verify(mockSecondaryTS, never()).fire(1);
  }

  @Test
  public void fireTorpedo_All_Tries_AllTS_Even_If_One_Fails() {
    when(mockPrimaryTS.isEmpty()).thenReturn(false);
    when(mockPrimaryTS.fire(1)).thenReturn(false);
    when(mockSecondaryTS.isEmpty()).thenReturn(false);
    when(mockSecondaryTS.fire(1)).thenReturn(true);

    boolean result = ship.fireTorpedo(FiringMode.ALL);
    
    assertEquals(true, result);

    verify(mockPrimaryTS, times(1)).isEmpty();
    verify(mockPrimaryTS, times(1)).fire(1);
    verify(mockSecondaryTS, times(1)).isEmpty();
    verify(mockSecondaryTS, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Single_Try_The_SameTS() {
    when(mockPrimaryTS.isEmpty()).thenReturn(false);
    when(mockPrimaryTS.fire(1)).thenReturn(true);
    when(mockSecondaryTS.isEmpty()).thenReturn(true);
    when(mockSecondaryTS.fire(1)).thenReturn(false);

    ship.fireTorpedo(FiringMode.SINGLE);
    Boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    assertEquals(true, result);

    verify(mockPrimaryTS, times(2)).isEmpty();
    verify(mockPrimaryTS, times(2)).fire(1);
    verify(mockSecondaryTS, times(1)).isEmpty();
    verify(mockSecondaryTS, never()).fire(1);
  }

}
