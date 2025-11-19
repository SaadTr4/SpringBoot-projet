export interface User {
  id?: number;
  fullName?: string;
}

export interface Project {
  id?: number;
  name: string;
  description?: string;
  status?: string;
  projectManager?: User;
  users?: User[];
}


export interface ProjectDTO {
  id: number;
  name: string;
  description: string;
  status: string; // ou enum si tu as défini Status
  projectManagerName: string | null;
  usersCount: number;
}
