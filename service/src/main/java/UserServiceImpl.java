import com.example.proto.UserProto;
import com.example.proto.UserServiceGrpc;
import com.google.protobuf.UInt32Value;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger logger = Logger.getLogger(Service.class.getName());

    private final Map<Integer, UserProto.User> map = new ConcurrentHashMap<>();

    @Override
    public void getUser(UInt32Value request, StreamObserver<UserProto.User> responseObserver) {
        logger.info("Request to get user: " + request.getValue());
        responseObserver.onNext(map.get(request.getValue()));
        responseObserver.onCompleted();
    }

    @Override
    public void addUser(UserProto.User request, StreamObserver<UInt32Value> responseObserver) {
        logger.info("Request to add user: " + request.getId());
        map.put(request.getId(), request);
        responseObserver.onNext(UInt32Value.of(request.getId()));
        responseObserver.onCompleted();
    }
}
