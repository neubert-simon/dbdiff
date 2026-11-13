package db.connection;

import db.connection.configuration.model.ConnectionConfiguration;
import db.exceptions.DriverUnableToConnectException;
import db.exceptions.SQLExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionDispatcherServiceLoaderTest {

    private final String TEST_URL = "jdbc:h2:mem:test";
    private final Properties TEST_PROPERTIES = new Properties();

    @Mock
    private ConnectionConfiguration configurationMock;
    @Mock
    private Driver driverMock;
    @Mock
    private Connection connectionMock;
    MockedStatic<SQLExceptionHandler> sqlExceptionHandlerMock  = mockStatic(SQLExceptionHandler.class, Mockito.CALLS_REAL_METHODS);

    private ConnectionDispatcherServiceLoader dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new ConnectionDispatcherServiceLoader(configurationMock, driverMock);
    }

    @AfterEach
    void tearDown() {
        sqlExceptionHandlerMock.close();
    }

    //region establishConnection
    @Test
    void testEstablishConnection_withValidConfiguration_returnsConnection() throws Exception {
        setUpValidConnection();
        Connection result = dispatcher.establishConnection();
        assertSame(connectionMock, result);
    }

    @Test
    void testEstablishConnection_withValidConfiguration_passesCorrectArgumentsToDriver() throws Exception {
        setUpValidConnection();
        dispatcher.establishConnection();
        verify(driverMock).connect(eq(TEST_URL), eq(TEST_PROPERTIES));
    }

    @Test
    void testEstablishConnection_whenDriverReturnsNull_throwsDriverUnableToConnectException() throws Exception {
        setUpNullConnection();
        assertThrows(DriverUnableToConnectException.class, dispatcher::establishConnection);
    }

    @Test
    void establishConnection_handlesSQLException() throws Exception {
        setUpConfigurationMock();
        SQLException original = new SQLException("Original", "08001");
        SQLException actionable = new SQLException("New", "08001");

        when(driverMock.connect(anyString(), any(Properties.class))).thenThrow(original);
        sqlExceptionHandlerMock.when(() -> SQLExceptionHandler.createActionableSQLException(original, "connection establishment")).thenReturn(actionable);

        SQLException thrown = assertThrows(
                SQLException.class,
                dispatcher::establishConnection
        );

        assertSame(actionable, thrown);
    }

    @Test
    void establishConnection_canBeCalledMultipleTimes() throws Exception {
        setUpConfigurationMock();
        Connection first = mock(Connection.class);
        Connection second = mock(Connection.class);

        when(driverMock.connect(anyString(), any(Properties.class)))
                .thenReturn(first, second);

        Connection result1 = dispatcher.establishConnection();
        Connection result2 = dispatcher.establishConnection();

        assertSame(first, result1);
        assertSame(second, result2);

        verify(driverMock, times(2)).connect(anyString(), any(Properties.class));
    }

    @Test
    void establishConnection_doesNotCloseReturnedConnection() throws Exception {
        setUpValidConnection();
        dispatcher.establishConnection();
        verify(connectionMock, never()).close();
    }
    //endregion

    //region Constructor
    @Test
    void testConstructor_withNullConfiguration_throwsNullPointerException() {
        assertThrows(
                NullPointerException.class,
                () -> new ConnectionDispatcherServiceLoader(null, driverMock)
        );
    }

    @Test
    void testConstructor_withNullDriver_throwsNullPointerException() {
        assertThrows(
                NullPointerException.class,
                () -> new ConnectionDispatcherServiceLoader(configurationMock, null)
        );
    }
    //endregion

    //region Auxiliary
    private void setUpConfigurationMock() {
        when(configurationMock.getJDBCConnectionString()).thenReturn(TEST_URL);
        when(configurationMock.toProperties()).thenReturn(TEST_PROPERTIES);
    }

    void setUpValidConnection() throws SQLException {
        setUpConfigurationMock();
        when(driverMock.connect(anyString(), any(Properties.class))).thenReturn(connectionMock);
    }

    void setUpNullConnection() throws SQLException {
        setUpConfigurationMock();
        when(driverMock.connect(anyString(), any(Properties.class))).thenReturn(null);
    }
    //endregion
}
