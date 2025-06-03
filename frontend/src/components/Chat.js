import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import '../styles/Chat.css';

const Chat = () => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [selectedUser, setSelectedUser] = useState(null);
    const [users, setUsers] = useState([]);
    const { currentUser } = useAuth();
    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);

    useEffect(() => {
        connectWebSocket();
        fetchUsers();
        return () => {
            if (stompClient.current) {
                stompClient.current.disconnect();
            }
        };
    }, []);

    const connectWebSocket = () => {
        const socket = new SockJS('http://localhost:8080/ws');
        stompClient.current = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log('Connected to WebSocket');
                stompClient.current.subscribe(
                    `/user/${currentUser.username}/queue/messages`,
                    onMessageReceived
                );
            },
            onDisconnect: () => {
                console.log('Disconnected from WebSocket');
            }
        });

        stompClient.current.activate();
    };

    const onMessageReceived = (payload) => {
        const message = JSON.parse(payload.body);
        setMessages(prev => [...prev, message]);
        scrollToBottom();
    };

    const sendMessage = (e) => {
        e.preventDefault();
        if (!newMessage.trim() || !selectedUser) return;

        const message = {
            content: newMessage,
            receiver: selectedUser,
            type: 'TEXT'
        };

        stompClient.current.publish({
            destination: '/app/chat.send',
            body: JSON.stringify(message)
        });

        setNewMessage('');
    };

    const fetchUsers = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/users', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            const data = await response.json();
            setUsers(data);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
    };

    const fetchMessages = async (userId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/chat/messages/${userId}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });
            const data = await response.json();
            setMessages(data);
            scrollToBottom();
        } catch (error) {
            console.error('Error fetching messages:', error);
        }
    };

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    return (
        <div className="chat-container">
            <div className="users-list">
                <h2>Contacts</h2>
                {users.map(user => (
                    <div
                        key={user.id}
                        className={`user-item ${selectedUser?.id === user.id ? 'selected' : ''}`}
                        onClick={() => {
                            setSelectedUser(user);
                            fetchMessages(user.id);
                        }}
                    >
                        <div className="user-avatar">
                            {user.profilePicture ? (
                                <img src={user.profilePicture} alt={user.username} />
                            ) : (
                                <div className="avatar-placeholder">
                                    {user.username[0].toUpperCase()}
                                </div>
                            )}
                        </div>
                        <div className="user-info">
                            <h3>{user.fullName}</h3>
                            <p>{user.status || 'Available'}</p>
                        </div>
                    </div>
                ))}
            </div>
            <div className="chat-main">
                {selectedUser ? (
                    <>
                        <div className="chat-header">
                            <h2>{selectedUser.fullName}</h2>
                        </div>
                        <div className="messages-container">
                            {messages.map((message, index) => (
                                <div
                                    key={index}
                                    className={`message ${message.sender.id === currentUser.id ? 'sent' : 'received'}`}
                                >
                                    <div className="message-content">
                                        {message.content}
                                    </div>
                                    <div className="message-time">
                                        {new Date(message.timestamp).toLocaleTimeString()}
                                    </div>
                                </div>
                            ))}
                            <div ref={messagesEndRef} />
                        </div>
                        <form onSubmit={sendMessage} className="message-input">
                            <input
                                type="text"
                                value={newMessage}
                                onChange={(e) => setNewMessage(e.target.value)}
                                placeholder="Type a message..."
                            />
                            <button type="submit">Send</button>
                        </form>
                    </>
                ) : (
                    <div className="no-chat-selected">
                        <h2>Select a contact to start chatting</h2>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Chat; 