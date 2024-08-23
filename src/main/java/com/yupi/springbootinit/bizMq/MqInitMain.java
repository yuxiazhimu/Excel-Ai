package com.yupi.springbootinit.bizMq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MqInitMain {
    public static void main(String[] args) {
        try {
            ConnectionFactory connectionFactory=new ConnectionFactory();
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(BiMqConstant.BI_EXCHANGE_NAME,"direct");
            channel.queueDeclare(BiMqConstant.BI_QUEUE_NAME,true,false,false,null);
            channel.queueBind(BiMqConstant.BI_QUEUE_NAME,BiMqConstant.BI_EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY);

        } catch (Exception e) {

        }
    }
}
