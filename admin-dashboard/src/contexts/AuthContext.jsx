import { createContext, useContext, useEffect, useRef, useState } from 'react'
import { supabase } from '../lib/supabase'

const AuthContext = createContext({})

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

// Admin rights are stored server-side in public.admins (see sql/001_admin_rls.sql).
// RLS only lets a user read their own row, so a row back means "is admin".
const checkIsAdmin = async (userId) => {
  if (!userId) return false
  const { data, error } = await supabase
    .from('admins')
    .select('user_id')
    .eq('user_id', userId)
    .maybeSingle()

  if (error) {
    console.error('Error checking admin status:', error)
    return false
  }
  return !!data
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [session, setSession] = useState(null)
  const [isAdmin, setIsAdmin] = useState(false)
  const [loading, setLoading] = useState(true)
  const userIdRef = useRef(null)

  useEffect(() => {
    const applySession = async (session) => {
      const userId = session?.user?.id ?? null
      userIdRef.current = userId
      const admin = await checkIsAdmin(userId)
      // A newer auth event (e.g. sign-out right after sign-in) superseded this one
      if (userIdRef.current !== userId) return
      setSession(session)
      setUser(session?.user ?? null)
      setIsAdmin(admin)
      setLoading(false)
    }

    // Get initial session
    const getInitialSession = async () => {
      const { data: { session }, error } = await supabase.auth.getSession()
      if (error) {
        console.error('Error getting session:', error)
        setLoading(false)
      } else {
        await applySession(session)
      }
    }

    getInitialSession()

    // Listen for auth changes. Only re-check admin status when the signed-in
    // user changes (not on token refresh), so the dashboard isn't unmounted.
    // Supabase warns against awaiting supabase calls inside this callback,
    // so the lookup is deferred.
    const { data: { subscription } } = supabase.auth.onAuthStateChange(
      (event, session) => {
        const newUserId = session?.user?.id ?? null
        if (newUserId === userIdRef.current) {
          setSession(session)
          setUser(session?.user ?? null)
          return
        }
        userIdRef.current = newUserId
        setLoading(true)
        setTimeout(() => applySession(session), 0)
      }
    )

    return () => subscription?.unsubscribe()
  }, [])

  const signIn = async (email, password) => {
    try {
      const { data, error } = await supabase.auth.signInWithPassword({
        email,
        password
      })

      if (error) throw error

      if (!(await checkIsAdmin(data.user?.id))) {
        await supabase.auth.signOut()
        throw new Error('This account does not have admin access.')
      }

      return { data, error: null }
    } catch (error) {
      return { data: null, error }
    }
  }

  const signOut = async () => {
    try {
      const { error } = await supabase.auth.signOut()
      if (error) throw error
      
      setUser(null)
      setSession(null)
      setIsAdmin(false)

      return { error: null }
    } catch (error) {
      return { error }
    }
  }

  const resetPassword = async (email) => {
    try {
      const { data, error } = await supabase.auth.resetPasswordForEmail(email, {
        redirectTo: `${window.location.origin}/reset-password`
      })

      if (error) throw error

      return { data, error: null }
    } catch (error) {
      return { data: null, error }
    }
  }

  const updatePassword = async (password) => {
    try {
      const { data, error } = await supabase.auth.updateUser({
        password
      })

      if (error) throw error

      return { data, error: null }
    } catch (error) {
      return { data: null, error }
    }
  }

  const value = {
    user,
    session,
    loading,
    signIn,
    signOut,
    resetPassword,
    updatePassword,
    isAuthenticated: !!session,
    isAdmin,
    userRole: isAdmin ? 'admin' : null,
    adminProfile: isAdmin ? {
      full_name: user?.email?.split('@')[0] || 'Admin User',
      role: 'admin',
      departments: { name: 'All Departments' }
    } : null
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}
