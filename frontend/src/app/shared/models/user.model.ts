export interface User {
  email: string;
  role: 'ARTIST' | 'ORGANIZER' | 'ADMIN';
  token?: string;
}
