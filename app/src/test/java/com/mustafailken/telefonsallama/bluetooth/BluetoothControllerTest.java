
package com.mustafailken.telefonsallama.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Toast;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.HashSet;
import java.util.Set;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PrepareForTest({BluetoothAdapter.class, Toast.class})
public class BluetoothControllerTest {


    @Test
    public void whenTryCheckThatBluetoothIsEnabledShouldCheckThatMethodReturnCorrectData() throws Exception {
        Activity activity = mock(Activity.class);
        BluetoothDiscoveryDeviceListener listener = mock(BluetoothDiscoveryDeviceListener.class);
        BluetoothAdapter adapter = mock(BluetoothAdapter.class);
        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(adapter);
        when(adapter.isEnabled()).thenReturn(true);
        BluetoothController controller = new BluetoothController(activity, adapter, listener);
        assertThat(controller.isBluetoothEnabled(), is(true));
    }


    @Test
    public void whenTryEnableBluetoothShouldCheckThatBluetoothAdapterCallEnableMethod() throws Exception {
        Activity activity = mock(Activity.class);
        BluetoothDiscoveryDeviceListener listener = mock(BluetoothDiscoveryDeviceListener.class);
        BluetoothAdapter adapter = mock(BluetoothAdapter.class);
        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(adapter);
        BluetoothController controller = new BluetoothController(activity, adapter, listener);
        controller.turnOnBluetooth();
        verify(adapter).enable();
    }


    @Test
    public void whenTryCallDiscoveringMethodOnBluetoothShouldCheckThatIsCalled() throws Exception {
        Activity activity = mock(Activity.class);
        BluetoothDiscoveryDeviceListener listener = mock(BluetoothDiscoveryDeviceListener.class);
        BluetoothAdapter adapter = mock(BluetoothAdapter.class);
        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(adapter);
        BluetoothController controller = new BluetoothController(activity, adapter, listener);
        controller.isDiscovering();
        verify(adapter).isDiscovering();
    }



    @Test(expected = IllegalStateException.class)
    public void whenTryBoundingDeviceButDeviceIsNullShouldCheckThatThrowException() throws Exception {
        Activity activity = mock(Activity.class);
        BluetoothDiscoveryDeviceListener listener = mock(BluetoothDiscoveryDeviceListener.class);
        BluetoothAdapter adapter = mock(BluetoothAdapter.class);
        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(adapter);
        BluetoothController controller = new BluetoothController(activity, adapter, listener);
        controller.getPairingDeviceStatus();
    }


    @Test
    public void whenTryGetDeviceNameShouldCheckThatAllIsOK() throws Exception {
        BluetoothDevice device = mock(BluetoothDevice.class);
        when(device.getName()).thenReturn("android");
        assertThat(BluetoothController.getDeviceName(device), is("android"));
    }


    @Test
    public void whenTryGetDeviceNameButItIsNullShouldCheckThatControllerReturnAddress() throws Exception {
        BluetoothDevice device = mock(BluetoothDevice.class);
        when(device.getName()).thenReturn(null);
        when(device.getName()).thenReturn("08-ED-B9-49-B2-E5");
        assertThat(BluetoothController.getDeviceName(device), is("08-ED-B9-49-B2-E5"));
    }


    @Test
    public void whenTryCheckThatDevicesAlreadyPairedShouldCheckThatMethodWorksCorrect() throws Exception {
        Activity activity = mock(Activity.class);
        Set<BluetoothDevice> devices = new HashSet<>();
        BluetoothDevice device = mock(BluetoothDevice.class);
        devices.add(device);
        BluetoothDiscoveryDeviceListener listener = mock(BluetoothDiscoveryDeviceListener.class);
        BluetoothAdapter adapter = mock(BluetoothAdapter.class);
        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(adapter);
        when(adapter.getBondedDevices()).thenReturn(devices);
        BluetoothController controller = new BluetoothController(activity, adapter, listener);
        assertThat(controller.isAlreadyPaired(device), is(true));
    }



    @Test
    public void whenTryCastDeviceToStringShouldCheckThatAllIsOk() throws Exception {
        BluetoothDevice device = mock(BluetoothDevice.class);
        when(device.getName()).thenReturn("device");
        when(device.getAddress()).thenReturn("08-ED-B9-49-B2-E5");
        String actual = "[Address: 08-ED-B9-49-B2-E5, Name: device]";
        assertEquals(actual, BluetoothController.deviceToString(device));
    }


    @Test
    public void whenTurnOnBluetoothAndScheduleDiscoveryShouldCheckThatBTenabled() throws Exception {
        Activity activity = mock(Activity.class);
        BluetoothDiscoveryDeviceListener listener = mock(BluetoothDiscoveryDeviceListener.class);
        BluetoothAdapter adapter = mock(BluetoothAdapter.class);
        mockStatic(BluetoothAdapter.class);
        when(BluetoothAdapter.getDefaultAdapter()).thenReturn(adapter);
        BluetoothController controller = new BluetoothController(activity, adapter, listener);
        controller.turnOnBluetoothAndScheduleDiscovery();
        verify(adapter).enable();
    }
}
