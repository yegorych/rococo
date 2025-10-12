package guru.qa.rococo.utils;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import io.grpc.*;

public class GrpcConsoleInterceptor implements ClientInterceptor {

    private static final JsonFormat.Printer printer = JsonFormat.printer();
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall(
                channel.newCall(methodDescriptor, callOptions)
        ) {

            @Override
            public void sendMessage(Object message) {
                try {
                    System.out.println("REQUEST: " +  printer.print((MessageOrBuilder) message));
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                super.sendMessage(message);
            }

            @Override
            public void start(Listener responseListener, Metadata headers) {
                ForwardingClientCallListener<Object> clientCallListener = new ForwardingClientCallListener<>() {

                    @Override
                    public void onMessage(Object message) {
                        try {
                            System.out.println("RESPONSE: " +  printer.print((MessageOrBuilder) message));
                        } catch (InvalidProtocolBufferException e) {
                            throw new RuntimeException(e);
                        }
                        super.onMessage(message);
                    }

                    @Override
                    protected Listener<Object> delegate() {
                        return responseListener;
                    }
                };
                super.start(clientCallListener, headers);
            }
        };
    }
}
