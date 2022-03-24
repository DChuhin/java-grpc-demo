import com.example.proto.UserProto;
import com.example.proto.UserServiceGrpc;
import com.google.protobuf.UInt32Value;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    @Override
    public StreamObserver<UserProto.User> addUsers(StreamObserver<UserProto.AddUsersResponse> responseObserver) {
        List<UInt32Value> addedUsersIds = new ArrayList<>();
        logger.info("Request to add users");
        return new StreamObserver<>() {
            @Override
            public void onNext(UserProto.User user) {
                logger.info("Adding user in stream: " + user.getName());
                map.put(user.getId(), user);
                addedUsersIds.add(UInt32Value.of(user.getId()));
            }

            @Override
            public void onError(Throwable t) {
                logger.warning("Error: " + t.getMessage());
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                var response = UserProto.AddUsersResponse.newBuilder()
                    .addAllUsers(addedUsersIds)
                    .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void getUsers(UserProto.GetUsersParams request, StreamObserver<UserProto.User> responseObserver) {
        map.values().forEach(user -> {
            try {
                logger.info("Send user " + user.getName());
                responseObserver.onNext(user);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        });
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<UserProto.FindUsersParams> findUsers(StreamObserver<UserProto.FindUsersResponse> responseObserver) {
        logger.info("Request to find users");
        return new StreamObserver<>() {
            @Override
            public void onNext(UserProto.FindUsersParams value) {
                logger.info("Find users " + value.getNameStartsWith());
                var users = map.values().stream()
                    .filter(user -> user.getName().startsWith(value.getNameStartsWith()))
                    .collect(Collectors.toList());
                responseObserver.onNext(
                    UserProto.FindUsersResponse.newBuilder()
                        .addAllUsers(users)
                        .build()
                );
            }

            @Override
            public void onError(Throwable t) {
                logger.info("error");
            }

            @Override
            public void onCompleted() {
                logger.info("complete");
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void encodingsDemo(UserProto.EncodingsDemoParams request, StreamObserver<UserProto.EncodingsDemoResponse> responseObserver) {
        logger.info("encodings");
        responseObserver.onNext(UserProto.EncodingsDemoResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
