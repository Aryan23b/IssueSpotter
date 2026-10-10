import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { Box, Button, CircularProgress, Typography } from '@mui/material'

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading, isAdmin, signOut } = useAuth()
  const location = useLocation()

  if (loading) {
    return (
      <Box 
        display="flex" 
        flexDirection="column"
        justifyContent="center" 
        alignItems="center" 
        minHeight="100vh"
        gap={2}
      >
        <CircularProgress size={60} />
        <Typography variant="h6" color="textSecondary">
          Loading IssueSpotter Admin...
        </Typography>
      </Box>
    )
  }

  if (!isAuthenticated) {
    // Redirect to login page with return url
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  if (!isAdmin) {
    return (
      <Box 
        display="flex" 
        flexDirection="column"
        justifyContent="center" 
        alignItems="center" 
        minHeight="100vh"
        gap={2}
      >
        <Typography variant="h4" color="error">
          Access Denied
        </Typography>
        <Typography variant="body1" color="textSecondary">
          You don't have admin privileges to access this dashboard.
        </Typography>
        <Button variant="outlined" onClick={signOut}>
          Sign out
        </Button>
      </Box>
    )
  }

  return children
}

export default ProtectedRoute
