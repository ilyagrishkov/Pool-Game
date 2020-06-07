package nl.tudelft.cse.sem.server.test.http.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.testing.NullPointerTester;
import io.javalin.http.Context;
import io.javalin.http.util.ContextUtil;
import io.javalin.plugin.json.JavalinJson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.cse.sem.server.Server;
import nl.tudelft.cse.sem.server.entity.User;
import nl.tudelft.cse.sem.server.entity.store.UserStore;
import nl.tudelft.cse.sem.server.http.endpoint.Endpoint;
import nl.tudelft.cse.sem.server.jwt.JWTManager;
import nl.tudelft.cse.sem.server.storage.cache.Cache;
import nl.tudelft.cse.sem.server.storage.database.Database;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public abstract class EndpointTest {
    protected transient Server server;
    protected transient JWTManager jwtManager;
    protected transient Database database;
    protected transient Cache cache;
    protected transient Context context;
    protected transient HttpServletResponse res;
    protected transient HttpServletRequest req;
    protected transient User user;
    protected transient Endpoint endpoint;
    protected transient UserStore userStore;

    /**
     * Sets up dependencies for base endpoint.
     */
    @BeforeEach
    public void setUp() {
        this.server = mock(Server.class);
        this.jwtManager = mock(JWTManager.class);
        this.userStore = mock(UserStore.class);
        this.database = mock(Database.class);
        this.cache = mock(Cache.class);
        this.res = mock(HttpServletResponse.class);
        this.req = mock(HttpServletRequest.class);
        this.context = ContextUtil.init(this.req, this.res);
        this.user = User.builder()
            .uuid(UUID.randomUUID())
            .loginName("yeet")
            .displayName("dany").build();

        when(this.server.getDatabase()).thenReturn(this.database);
        when(this.database.getUserStore()).thenReturn(this.userStore);
        when(this.server.getCache()).thenReturn(this.cache);
        when(this.server.getJwtManager()).thenReturn(this.jwtManager);
        when(this.jwtManager.getUserFromContext(this.context)).thenReturn(Optional.of(this.user));
    }

    @Test
    public void testNullPointers() {
        NullPointerTester tester = new NullPointerTester();

        tester.setDefault(Context.class, this.context);
        tester.setDefault(Server.class, this.server);
        tester.testAllPublicStaticMethods(this.endpoint.getClass());
        tester.testAllPublicInstanceMethods(this.endpoint);
    }

    protected void setRequestBody(Object object) throws IOException {
        when(this.req.getInputStream()).thenReturn(createServletInputStream(object));
    }

    protected void verifyStatusCode(int code) {
        // Get arguments and verify correct status was set
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);

        verify(this.res, times(1)).setStatus(statusCaptor.capture());
        assertEquals(code, statusCaptor.getValue());
    }

    protected void verifyBody(Object object) {
        assertTrue(compareObjectToStream(this.context.resultStream(), object));
    }

    private boolean compareObjectToStream(InputStream inputStream, Object object) {
        try {
            return IOUtils
                .contentEquals(IOUtils
                        .toInputStream(JavalinJson.toJson(object),
                            Charset.defaultCharset()),
                    inputStream);
        } catch (IOException e) {
            return false;
        }
    }

    // PMD can't handle 'new' java 8 things
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    private ServletInputStream createServletInputStream(Object object) throws IOException {
        final byte[] bytes = IOUtils
            .toByteArray(IOUtils.toInputStream(JavalinJson.toJson(object),
                Charset.defaultCharset()));

        return new ServletInputStream() {
            private int lastIndexRetrieved = -1;
            private ReadListener readListener = null;

            @Override
            public boolean isFinished() {
                return lastIndexRetrieved == bytes.length - 1;
            }

            @Override
            public boolean isReady() {
                return isFinished();
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                this.readListener = readListener;
                try {
                    if (isFinished()) {
                        readListener.onAllDataRead();
                    } else {
                        readListener.onDataAvailable();
                    }
                } catch (IOException e) {
                    readListener.onError(e);
                }
            }

            @Override
            public int read() throws IOException {
                if (!isFinished()) {
                    int i = bytes[lastIndexRetrieved + 1];
                    lastIndexRetrieved++;
                    if (isFinished() && (readListener != null)) {
                        try {
                            readListener.onAllDataRead();
                        } catch (IOException ex) {
                            readListener.onError(ex);
                            throw ex;
                        }
                    }
                    return i;
                } else {
                    return -1;
                }
            }
        };
    }
}
