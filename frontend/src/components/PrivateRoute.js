import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const PrivateRoute = ({ component: Component, ...rest }) => {
    const { currentUser, loading } = useAuth();

    return (
        <Route
            {...rest}
            render={props => {
                if (loading) {
                    return <div>Loading...</div>;
                }
                return currentUser ? (
                    <Component {...props} />
                ) : (
                    <Redirect to="/login" />
                );
            }}
        />
    );
};

export default PrivateRoute; 